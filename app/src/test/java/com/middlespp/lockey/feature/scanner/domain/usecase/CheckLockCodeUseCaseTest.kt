package com.middlespp.lockey.feature.scanner.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.scanner.domain.model.LockCodeCheckResult
import com.middlespp.lockey.feature.scanner.domain.model.ScannedLockCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant

class CheckLockCodeUseCaseTest {
    private val useCase = CheckLockCodeUseCase()

    @Test
    fun `returns valid when scanned lock matches pass lock`() {
        val result = useCase(
            pass = accessPass(lockId = "studio-a1"),
            scannedLockCode = ScannedLockCode(lockId = "studio-a1", lockCode = "A1B2C3")
        )

        val valid = assertIs<LockCodeCheckResult.Valid>(result)
        assertEquals("studio-a1", valid.lockId)
        assertEquals("A1B2C3", valid.lockCode)
    }

    @Test
    fun `returns mismatch when scanned lock differs from pass lock`() {
        val result = useCase(
            pass = accessPass(lockId = "studio-a1"),
            scannedLockCode = ScannedLockCode(lockId = "studio-b2", lockCode = "A1B2C3")
        )

        val mismatch = assertIs<LockCodeCheckResult.LockMismatch>(result)
        assertEquals("studio-a1", mismatch.expectedLockId)
        assertEquals("studio-b2", mismatch.scannedLockId)
    }

    private fun accessPass(lockId: String): AccessPass = AccessPass(
        lockId = lockId,
        accessCode = "ABCDEF1234",
        bookingStartsAt = Instant.parse("2026-04-22T12:00:00Z"),
        bookingEndsAt = Instant.parse("2026-04-22T14:00:00Z")
    )
}
