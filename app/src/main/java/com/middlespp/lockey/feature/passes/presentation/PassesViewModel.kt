package com.middlespp.lockey.feature.passes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.usecase.DeletePassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.GetPassesUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.ImportPassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.SetPassPinnedUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.UpdatePassOrderUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PassesViewModel(
    private val getPasses: GetPassesUseCase,
    private val deletePass: DeletePassUseCase,
    private val setPassPinned: SetPassPinnedUseCase,
    private val updatePassOrder: UpdatePassOrderUseCase,
    private val importPass: ImportPassUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PassesUiState())
    val state: StateFlow<PassesUiState> = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<PassesUiEvent>()
    val uiEvents: SharedFlow<PassesUiEvent> = _uiEvents.asSharedFlow()

    private var lastDeletedPass: AccessPass? = null

    init {
        viewModelScope.launch {
            getPasses()
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { passes ->
                    _state.update {
                        it.copy(
                            passes = passes,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun delete(lockId: String) {
        val pass = state.value.passes.firstOrNull { it.lockId == lockId } ?: return
        viewModelScope.launch {
            lastDeletedPass = pass
            deletePass(lockId)
            _uiEvents.emit(PassesUiEvent.PassDeleted(lockId))
        }
    }

    fun undoDelete() {
        val pass = lastDeletedPass ?: return
        viewModelScope.launch {
            importPass.save(pass)
            lastDeletedPass = null
        }
    }

    fun togglePinned(lockId: String) {
        val pass = state.value.passes.firstOrNull { it.lockId == lockId } ?: return
        viewModelScope.launch {
            setPassPinned(lockId, !pass.isPinned)
        }
    }

    fun move(fromIndex: Int, toIndex: Int) {
        val passes = state.value.passes.toMutableList()
        if (fromIndex !in passes.indices || toIndex !in passes.indices) return

        val pass = passes.removeAt(fromIndex)
        passes.add(toIndex, pass)

        viewModelScope.launch {
            updatePassOrder(passes)
        }
    }

    companion object {
        fun factory(
            getPasses: GetPassesUseCase,
            deletePass: DeletePassUseCase,
            setPassPinned: SetPassPinnedUseCase,
            updatePassOrder: UpdatePassOrderUseCase,
            importPass: ImportPassUseCase
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = PassesViewModel(
                getPasses = getPasses,
                deletePass = deletePass,
                setPassPinned = setPassPinned,
                updatePassOrder = updatePassOrder,
                importPass = importPass
            ) as T
        }
    }
}
