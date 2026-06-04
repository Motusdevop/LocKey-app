package com.middlespp.lockey.feature.scanner.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.scanner.domain.model.LockCodeCheckResult
import com.middlespp.lockey.feature.scanner.domain.model.ScannedLockCode

class CheckLockCodeUseCase {
    operator fun invoke(
        pass: AccessPass,
        scannedLockCode: ScannedLockCode
    ): LockCodeCheckResult {
        if (pass.lockId != scannedLockCode.lockId) {
            return LockCodeCheckResult.LockMismatch(
                expectedLockId = pass.lockId,
                scannedLockId = scannedLockCode.lockId
            )
        }

        return LockCodeCheckResult.Valid(
            lockId = scannedLockCode.lockId,
            lockCode = scannedLockCode.lockCode
        )
    }
}
