package com.middlespp.lockey.feature.passes.domain.model

import com.middlespp.lockey.core.config.AccessConfig
import kotlin.time.Instant

data class AccessPass(
    val lockId: String,
    val accessCode: String,
    val bookingStartsAt: Instant,
    val bookingEndsAt: Instant,
    val isPinned: Boolean = false,
    val sortOrder: Int = 0
) {
    init {
        require(lockId.isNotBlank())
        require(accessCode.isNotBlank())
        require(bookingEndsAt > bookingStartsAt)
    }

    val validFrom: Instant = bookingStartsAt - AccessConfig.bookingStartGracePeriod
    val validUntil: Instant = bookingEndsAt

    fun isValidAt(now: Instant): Boolean = now in validFrom..validUntil
}
