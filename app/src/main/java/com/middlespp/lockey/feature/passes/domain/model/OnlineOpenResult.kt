package com.middlespp.lockey.feature.passes.domain.model

sealed interface OnlineOpenResult {
    data class CommandSent(val command: OpenCommand) : OnlineOpenResult
    data object InvalidLockCode : OnlineOpenResult
    data object InvalidAccessCode : OnlineOpenResult
    data object BookingWindowClosed : OnlineOpenResult
    data object InvalidRequest : OnlineOpenResult
    data object NetworkError : OnlineOpenResult
    data object UnknownError : OnlineOpenResult
}
