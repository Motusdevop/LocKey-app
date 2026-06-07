package com.middlespp.lockey.core.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Navigator {
    private val _backStack = MutableStateFlow<List<Screen>>(listOf(Screen.Passes))
    val backStack: StateFlow<List<Screen>> = _backStack.asStateFlow()

    fun navigate(screen: Screen) {
        _backStack.update { currentBackStack -> currentBackStack + screen }
    }

    fun replaceAll(vararg screens: Screen) {
        _backStack.value = screens.toList().takeIf { it.isNotEmpty() } ?: listOf(Screen.Passes)
    }

    fun pop() {
        _backStack.update { currentBackStack ->
            if (currentBackStack.size > MIN_BACK_STACK_SIZE) {
                currentBackStack.dropLast(1)
            } else {
                currentBackStack
            }
        }
    }

    private companion object {
        const val MIN_BACK_STACK_SIZE = 1
    }
}
