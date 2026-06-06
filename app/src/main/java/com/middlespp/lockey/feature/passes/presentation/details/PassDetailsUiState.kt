package com.middlespp.lockey.feature.passes.presentation.details

import com.middlespp.lockey.feature.passes.domain.model.PassDetails

data class PassDetailsUiState(
    val details: PassDetails? = null,
    val isLoading: Boolean = true
)
