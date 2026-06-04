package com.middlespp.lockey.feature.passes.domain.model

data class OpenCommand(
    val lockId: String,
    val commandId: String
) {
    init {
        require(lockId.isNotBlank())
        require(commandId.isNotBlank())
    }
}
