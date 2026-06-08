package com.middlespp.lockey.feature.scanner.presentation

data class ScannerUiState(
    val lockId: String? = null,
    val code: String = "",
    val message: String = "Отсканируй или вставь QR-код LocKey.",
    val isBusy: Boolean = false,
    val isSuccess: Boolean = false
)
