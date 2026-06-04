package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.model.OnlineOpenResult
import com.middlespp.lockey.feature.passes.domain.model.OpenLockResult
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository
import com.middlespp.lockey.feature.scanner.domain.model.LockCodeCheckResult
import com.middlespp.lockey.feature.scanner.domain.model.ScannedLockCode
import com.middlespp.lockey.feature.scanner.domain.usecase.CheckLockCodeUseCase

class OpenLockUseCase(
    private val checkLockCode: CheckLockCodeUseCase,
    private val accessRepository: AccessRepository
) {
    suspend operator fun invoke(
        pass: AccessPass,
        scannedLockCode: ScannedLockCode
    ): OpenLockResult = when (val checkResult = checkLockCode(pass, scannedLockCode)) {
        is LockCodeCheckResult.Valid -> accessRepository.verifyAccess(
            pass = pass,
            lockCode = checkResult.lockCode
        ).toOpenLockResult()

        is LockCodeCheckResult.LockMismatch -> OpenLockResult.LockMismatch(
            expectedLockId = checkResult.expectedLockId,
            scannedLockId = checkResult.scannedLockId
        )
    }

    private fun OnlineOpenResult.toOpenLockResult(): OpenLockResult = when (this) {
        is OnlineOpenResult.CommandSent -> OpenLockResult.CommandSent(command)
        OnlineOpenResult.InvalidLockCode -> OpenLockResult.InvalidLockCode
        OnlineOpenResult.InvalidAccessCode -> OpenLockResult.InvalidAccessCode
        OnlineOpenResult.BookingWindowClosed -> OpenLockResult.BookingWindowClosed
        OnlineOpenResult.InvalidRequest -> OpenLockResult.InvalidRequest
        OnlineOpenResult.UnknownError -> OpenLockResult.UnknownError
        OnlineOpenResult.NetworkError -> OpenLockResult.NetworkError
    }
}
