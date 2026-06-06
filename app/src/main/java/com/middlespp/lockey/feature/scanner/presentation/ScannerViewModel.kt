package com.middlespp.lockey.feature.scanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.middlespp.lockey.feature.passes.domain.model.ImportPassResult
import com.middlespp.lockey.feature.passes.domain.model.OpenLockResult
import com.middlespp.lockey.feature.passes.domain.usecase.GetPassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.ImportPassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.OpenLockUseCase
import com.middlespp.lockey.feature.scanner.domain.parse.LockQrParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val lockId: String?,
    private val getPass: GetPassUseCase,
    private val importPass: ImportPassUseCase,
    private val openLock: OpenLockUseCase,
    private val qrParser: LockQrParser
) : ViewModel() {

    private val _state = MutableStateFlow(ScannerUiState(lockId = lockId))
    val state: StateFlow<ScannerUiState> = _state.asStateFlow()

    fun onCodeChange(value: String) {
        _state.update { it.copy(code = value) }
    }

    fun onScannedCode(value: String) {
        onCodeChange(value)
        submit()
    }

    fun submit() {
        val value = state.value.code.trim()
        if (value.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isBusy = true, message = "Checking LocKey access...") }
            val message = if (lockId == null) {
                when (importPass(value)) {
                    is ImportPassResult.Saved -> "Pass imported successfully."
                    ImportPassResult.InvalidLink -> "This QR payload is not a valid LocKey pass."
                }
            } else {
                val details = getPass(lockId)
                val scannedCode = qrParser.parse(value)
                when {
                    details == null -> "Pass not found."
                    scannedCode == null -> "This QR payload is not a valid lock code."
                    else -> openLock(details.pass, scannedCode).message()
                }
            }
            _state.update { it.copy(isBusy = false, message = message) }
        }
    }

    companion object {
        fun factory(
            lockId: String?,
            getPass: GetPassUseCase,
            importPass: ImportPassUseCase,
            openLock: OpenLockUseCase,
            qrParser: LockQrParser
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ScannerViewModel(
                lockId = lockId,
                getPass = getPass,
                importPass = importPass,
                openLock = openLock,
                qrParser = qrParser
            ) as T
        }
    }
}

private fun OpenLockResult.message(): String = when (this) {
    is OpenLockResult.CommandSent -> "Unlock command sent."
    is OpenLockResult.LockMismatch -> "This QR code belongs to another lock."
    OpenLockResult.InvalidLockCode -> "Lock code is invalid or expired."
    OpenLockResult.InvalidAccessCode -> "Access code is invalid."
    OpenLockResult.BookingWindowClosed -> "Booking window is closed."
    OpenLockResult.InvalidRequest -> "Unable to open this lock."
    OpenLockResult.NetworkError -> "Network error. Try again."
    OpenLockResult.UnknownError -> "Unknown error. Try again."
}
