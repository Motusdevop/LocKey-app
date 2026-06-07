package com.middlespp.lockey.feature.scanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.middlespp.lockey.feature.passes.domain.model.ImportPassResult
import com.middlespp.lockey.feature.passes.domain.model.OpenLockResult
import com.middlespp.lockey.feature.passes.domain.usecase.GetPassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.ImportPassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.OpenLockUseCase
import com.middlespp.lockey.feature.scanner.domain.model.ScannedLockCode
import com.middlespp.lockey.feature.scanner.domain.parse.LockQrParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPassViewModel(
    private val importPass: ImportPassUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(
        ScannerUiState(message = "Открой ссылку LocKey снаружи приложения или вставь ее сюда вручную.")
    )
    val state: StateFlow<ScannerUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AddPassUiEvent>()
    val events: SharedFlow<AddPassUiEvent> = _events.asSharedFlow()

    fun onLinkChange(value: String) {
        _state.update { it.copy(code = value) }
    }

    fun submit() {
        submit(state.value.code)
    }

    private fun submit(rawValue: String) {
        val value = rawValue.trim()
        if (value.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isBusy = true, message = "Добавляем пропуск...") }
            val message = when (importPass(value)) {
                is ImportPassResult.Saved -> {
                    _events.emit(AddPassUiEvent.PassAdded)
                    "Пропуск успешно добавлен."
                }
                ImportPassResult.InvalidLink -> "Это не похоже на ссылку пропуска LocKey."
            }
            _state.update { it.copy(isBusy = false, message = message) }
        }
    }

    companion object {
        fun factory(importPass: ImportPassUseCase): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AddPassViewModel(importPass) as T
        }
    }
}

sealed interface AddPassUiEvent {
    data object PassAdded : AddPassUiEvent
}

class OpenLockViewModel(
    private val lockId: String,
    private val getPass: GetPassUseCase,
    private val openLock: OpenLockUseCase,
    private val qrParser: LockQrParser
) : ViewModel() {

    private val _state = MutableStateFlow(
        ScannerUiState(
            lockId = lockId,
            message = "Отсканируй QR замка или введи код, который указан на замке."
        )
    )
    val state: StateFlow<ScannerUiState> = _state.asStateFlow()

    fun onCodeChange(value: String) {
        _state.update { it.copy(code = value) }
    }

    fun onScannedQr(value: String) {
        submitQr(value)
    }

    fun submitManualCode() {
        val code = state.value.code.trim()
        if (code.isBlank()) return
        openLockWith(ScannedLockCode(lockId = lockId, lockCode = code))
    }

    private fun submitQr(rawValue: String) {
        val value = rawValue.trim()
        if (value.isBlank()) return

        val scannedCode = qrParser.parse(value)
        if (scannedCode == null) {
            _state.update { it.copy(message = "Этот QR-код не является кодом замка.") }
            return
        }

        openLockWith(scannedCode)
    }

    private fun openLockWith(scannedCode: ScannedLockCode) {
        viewModelScope.launch {
            _state.update { it.copy(isBusy = true, message = "Проверяем код замка...") }
            val details = getPass(lockId)
            val message = when {
                details == null -> "Пропуск не найден."
                else -> openLock(details.pass, scannedCode).message()
            }
            _state.update { it.copy(isBusy = false, message = message) }
        }
    }

    companion object {
        fun factory(
            lockId: String,
            getPass: GetPassUseCase,
            openLock: OpenLockUseCase,
            qrParser: LockQrParser
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = OpenLockViewModel(
                lockId = lockId,
                getPass = getPass,
                openLock = openLock,
                qrParser = qrParser
            ) as T
        }
    }
}

private fun OpenLockResult.message(): String = when (this) {
    is OpenLockResult.CommandSent -> "Команда открытия отправлена."
    is OpenLockResult.LockMismatch -> "Этот код относится к другому замку."
    OpenLockResult.InvalidLockCode -> "Код замка недействителен или устарел."
    OpenLockResult.InvalidAccessCode -> "Код доступа недействителен."
    OpenLockResult.BookingWindowClosed -> "Окно бронирования закрыто."
    OpenLockResult.InvalidRequest -> "Не удалось открыть этот замок."
    OpenLockResult.NetworkError -> "Ошибка сети. Попробуй еще раз."
    OpenLockResult.UnknownError -> "Неизвестная ошибка. Попробуй еще раз."
}
