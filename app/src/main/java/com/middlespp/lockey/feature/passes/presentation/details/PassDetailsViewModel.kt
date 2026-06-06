package com.middlespp.lockey.feature.passes.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.middlespp.lockey.feature.passes.domain.usecase.GetPassUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PassDetailsViewModel(
    private val lockId: String,
    private val getPass: GetPassUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PassDetailsUiState())
    val state: StateFlow<PassDetailsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    details = getPass(lockId),
                    isLoading = false
                )
            }
        }
    }

    companion object {
        fun factory(lockId: String, getPass: GetPassUseCase): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = PassDetailsViewModel(
                lockId = lockId,
                getPass = getPass
            ) as T
        }
    }
}
