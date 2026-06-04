package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.model.OnlineOpenResult
import com.middlespp.lockey.feature.passes.domain.model.OpenCommand
import com.middlespp.lockey.feature.passes.domain.model.OpenLockResult
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository
import com.middlespp.lockey.feature.scanner.domain.model.ScannedLockCode
import com.middlespp.lockey.feature.scanner.domain.usecase.CheckLockCodeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

class OpenLockUseCaseTest {
    @Test
    fun `verifies access when scanned lock matches pass`() = runTest {
        val repository = FakeAccessRepository(
            result = OnlineOpenResult.CommandSent(OpenCommand(lockId = "studio-a1", commandId = "command-1"))
        )
        val useCase = OpenLockUseCase(
            checkLockCode = CheckLockCodeUseCase(),
            accessRepository = repository
        )

        val result = useCase(
            pass = accessPass(lockId = "studio-a1"),
            scannedLockCode = ScannedLockCode(lockId = "studio-a1", lockCode = "A1B2C3")
        )

        val sent = assertIs<OpenLockResult.CommandSent>(result)
        assertEquals("command-1", sent.command.commandId)
        assertTrue(repository.wasCalled)
        assertEquals("A1B2C3", repository.lockCode)
    }

    @Test
    fun `returns network error when backend is unavailable`() = runTest {
        val repository = FakeAccessRepository(result = OnlineOpenResult.NetworkError)
        val useCase = OpenLockUseCase(
            checkLockCode = CheckLockCodeUseCase(),
            accessRepository = repository
        )

        val result = useCase(
            pass = accessPass(lockId = "studio-a1"),
            scannedLockCode = ScannedLockCode(lockId = "studio-a1", lockCode = "A1B2C3")
        )

        assertIs<OpenLockResult.NetworkError>(result)
    }

    @Test
    fun `does not verify access when scanned lock does not match pass`() = runTest {
        val repository = FakeAccessRepository(result = OnlineOpenResult.UnknownError)
        val useCase = OpenLockUseCase(
            checkLockCode = CheckLockCodeUseCase(),
            accessRepository = repository
        )

        val result = useCase(
            pass = accessPass(lockId = "studio-a1"),
            scannedLockCode = ScannedLockCode(lockId = "studio-b2", lockCode = "A1B2C3")
        )

        val mismatch = assertIs<OpenLockResult.LockMismatch>(result)
        assertEquals("studio-a1", mismatch.expectedLockId)
        assertEquals("studio-b2", mismatch.scannedLockId)
        assertFalse(repository.wasCalled)
    }

    private fun accessPass(lockId: String): AccessPass = AccessPass(
        lockId = lockId,
        accessCode = "ABCDEF1234",
        bookingStartsAt = Instant.parse("2026-04-22T12:00:00Z"),
        bookingEndsAt = Instant.parse("2026-04-22T14:00:00Z")
    )

    private class FakeAccessRepository(
        private val result: OnlineOpenResult
    ) : AccessRepository {
        var wasCalled = false
            private set
        var lockCode: String? = null
            private set

        override suspend fun savePass(pass: AccessPass) = Unit

        override suspend fun getPass(lockId: String): AccessPass? = null

        override fun observePasses(): Flow<List<AccessPass>> = flowOf(emptyList())

        override suspend fun deletePass(lockId: String) = Unit

        override suspend fun setPassPinned(lockId: String, isPinned: Boolean) = Unit

        override suspend fun updatePassOrder(lockId: String, sortOrder: Int) = Unit

        override suspend fun verifyAccess(
            pass: AccessPass,
            lockCode: String
        ): OnlineOpenResult {
            wasCalled = true
            this.lockCode = lockCode
            return result
        }
    }
}
