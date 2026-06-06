package com.middlespp.lockey.feature.scanner.presentation

data class ScannerUiState(
    val lockId: String? = null,
    val code: String = "",
    val message: String = "Scan or paste a LocKey QR payload.",
    val isBusy: Boolean = false
)
