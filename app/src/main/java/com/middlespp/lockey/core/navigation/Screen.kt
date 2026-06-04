package com.middlespp.lockey.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Passes : Screen

    @Serializable
    data class PassDetails(val lockId: String) : Screen

    @Serializable
    data class Scanner(val lockId: String? = null) : Screen
}
