package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.model.OnlineOpenResult
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class GetPassUseCaseTest {
    @Test
    fun `returns pass details`() = runTest {
        val pass = accessPass(lockId = "studio-a1")
        val useCase = GetPassUseCase(FakeAccessRepository(passes = listOf(pass)))

        val result = useCase("studio-a1")

        assertEquals(pass, result?.pass)
    }

    @Test
    fun `returns null when pass is missing`() = runTest {
        val useCase = GetPassUseCase(FakeAccessRepository())

        val result = useCase("studio-a1")

        assertNull(result)
    }

    private class FakeAccessRepository(
        private val passes: List<AccessPass> = emptyList()
    ) : AccessRepository {
        override suspend fun savePass(pass: AccessPass) = Unit

        override suspend fun getPass(lockId: String): AccessPass? = passes.firstOrNull { it.lockId == lockId }

        override fun observePasses(): Flow<List<AccessPass>> = flowOf(passes)

        override suspend fun deletePass(lockId: String) = Unit

        override suspend fun setPassPinned(lockId: String, isPinned: Boolean) = Unit

        override suspend fun updatePassOrder(lockId: String, sortOrder: Int) = Unit

        override suspend fun verifyAccess(pass: AccessPass, lockCode: String): OnlineOpenResult =
            OnlineOpenResult.UnknownError
    }
}

private fun accessPass(lockId: String): AccessPass = AccessPass(
    lockId = lockId,
    accessCode = "ABCDEF1234",
    bookingStartsAt = Instant.parse("2026-04-22T12:00:00Z"),
    bookingEndsAt = Instant.parse("2026-04-22T14:00:00Z")
)
