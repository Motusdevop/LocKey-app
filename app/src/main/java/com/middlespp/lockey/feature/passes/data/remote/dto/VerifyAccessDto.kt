package com.middlespp.lockey.feature.passes.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyAccessRequestDto(
    @SerialName("access_code") val accessCode: String,
    @SerialName("lock_code") val lockCode: String,
    @SerialName("booking_starts_at") val bookingStartsAt: String,
    @SerialName("booking_ends_at") val bookingEndsAt: String
)

@Serializable
data class VerifyAccessResponseDto(
    val status: String,
    @SerialName("lock_id") val lockId: String,
    @SerialName("command_id") val commandId: String
)
