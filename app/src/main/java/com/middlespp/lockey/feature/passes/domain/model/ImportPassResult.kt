package com.middlespp.lockey.feature.passes.domain.model

sealed interface ImportPassResult {
    data class Saved(val pass: AccessPass) : ImportPassResult
    data object InvalidLink : ImportPassResult
}
