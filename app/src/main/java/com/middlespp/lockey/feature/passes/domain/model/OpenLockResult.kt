package com.middlespp.lockey.feature.passes.domain.model

sealed interface OpenLockResult {
    data class CommandSent(val command: OpenCommand) : OpenLockResult
    data class LockMismatch(val expectedLockId: String, val scannedLockId: String) : OpenLockResult
    data object InvalidLockCode : OpenLockResult
    data object InvalidAccessCode : OpenLockResult
    data object BookingWindowClosed : OpenLockResult
    data object InvalidRequest : OpenLockResult
    data object NetworkError : OpenLockResult
    data object UnknownError : OpenLockResult
}
