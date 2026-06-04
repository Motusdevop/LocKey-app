package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.model.OnlineOpenResult
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class GetPassesUseCaseTest {
    @Test
    fun `returns saved passes`() = runTest {
        val passes = listOf(
            accessPass(lockId = "studio-a1"),
            accessPass(lockId = "office-b2")
        )
        val useCase = GetPassesUseCase(FakeAccessRepository(passes))

        val result = useCase().first()

        assertEquals(passes, result)
    }

    private class FakeAccessRepository(
        private val passes: List<AccessPass>
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

    private fun accessPass(lockId: String): AccessPass = AccessPass(
        lockId = lockId,
        accessCode = "ABCDEF1234",
        bookingStartsAt = Instant.parse("2026-04-22T12:00:00Z"),
        bookingEndsAt = Instant.parse("2026-04-22T14:00:00Z")
    )
}
