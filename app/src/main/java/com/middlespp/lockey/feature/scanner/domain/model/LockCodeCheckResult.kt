package com.middlespp.lockey.feature.scanner.domain.model

sealed interface LockCodeCheckResult {
    data class Valid(val lockId: String, val lockCode: String) : LockCodeCheckResult
    data class LockMismatch(val expectedLockId: String, val scannedLockId: String) : LockCodeCheckResult
}
