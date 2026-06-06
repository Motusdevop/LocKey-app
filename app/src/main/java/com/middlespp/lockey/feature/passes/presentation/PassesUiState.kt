package com.middlespp.lockey.feature.passes.presentation

import com.middlespp.lockey.feature.passes.domain.model.AccessPass

data class PassesUiState(
    val passes: List<AccessPass> = emptyList(),
    val isLoading: Boolean = true
) {
    val pinnedCount: Int = passes.count { it.isPinned }
    val hasPasses: Boolean = passes.isNotEmpty()
}

sealed interface PassesUiEvent {
    data class PassDeleted(val lockId: String) : PassesUiEvent
    data object ImportFailed : PassesUiEvent
}
