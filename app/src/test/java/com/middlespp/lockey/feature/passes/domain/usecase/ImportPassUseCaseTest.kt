package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.model.ImportPassResult
import com.middlespp.lockey.feature.passes.domain.model.OnlineOpenResult
import com.middlespp.lockey.feature.passes.domain.parse.PassLinkParser
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Instant

class ImportPassUseCaseTest {
    @Test
    fun `saves valid pass link`() = runTest {
        val repository = FakeAccessRepository()
        val useCase = ImportPassUseCase(PassLinkParser(), repository)

        val result = useCase(validLink())

        val saved = assertIs<ImportPassResult.Saved>(result)
        assertEquals("studio-a1", saved.pass.lockId)
        assertEquals(saved.pass, repository.savedPass)
    }

    @Test
    fun `does not save invalid link`() = runTest {
        val repository = FakeAccessRepository()
        val useCase = ImportPassUseCase(PassLinkParser(), repository)

        val result = useCase("lockey://open?lock_id=studio-a1")

        assertIs<ImportPassResult.InvalidLink>(result)
        assertNull(repository.savedPass)
    }

    private fun validLink(): String = "lockey://open?lock_id=studio-a1&access_code=ABCDEF1234" +
        "&booking_starts_at=2026-04-22T12:00:00Z" +
        "&booking_ends_at=2026-04-22T14:00:00Z"

    private class FakeAccessRepository : AccessRepository {
        var savedPass: AccessPass? = null
            private set

        override suspend fun savePass(pass: AccessPass) {
            savedPass = pass
        }

        override suspend fun getPass(lockId: String): AccessPass? = null

        override fun observePasses(): Flow<List<AccessPass>> = flowOf(emptyList())

        override suspend fun deletePass(lockId: String) = Unit

        override suspend fun setPassPinned(lockId: String, isPinned: Boolean) = Unit

        override suspend fun updatePassOrder(lockId: String, sortOrder: Int) = Unit

        override suspend fun verifyAccess(
            pass: AccessPass,
            lockCode: String
        ): OnlineOpenResult = OnlineOpenResult.UnknownError
    }
}
