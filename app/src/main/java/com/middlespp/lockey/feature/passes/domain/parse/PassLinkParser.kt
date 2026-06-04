package com.middlespp.lockey.feature.passes.domain.parse

import com.middlespp.lockey.core.config.DeepLinkConfig
import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import java.net.URI
import kotlin.time.Instant

class PassLinkParser {
    fun parse(value: String): AccessPass? {
        val uri = runCatching { URI(value.trim()) }.getOrNull() ?: return null
        if (uri.scheme != DeepLinkConfig.SCHEME || uri.host != DeepLinkConfig.OPEN_HOST) return null

        val lockId = uri.queryParameter(DeepLinkConfig.LOCK_ID_QUERY) ?: return null
        val accessCode = uri.queryParameter(DeepLinkConfig.ACCESS_CODE_QUERY) ?: return null
        val bookingStartsAt = uri.instantParameter(DeepLinkConfig.BOOKING_STARTS_AT_QUERY) ?: return null
        val bookingEndsAt = uri.instantParameter(DeepLinkConfig.BOOKING_ENDS_AT_QUERY) ?: return null

        return runCatching {
            AccessPass(
                lockId = lockId,
                accessCode = accessCode,
                bookingStartsAt = bookingStartsAt,
                bookingEndsAt = bookingEndsAt
            )
        }.getOrNull()
    }

    private fun URI.instantParameter(name: String): Instant? = queryParameter(name)?.let { value ->
        runCatching { Instant.parse(value) }.getOrNull()
    }

    private fun URI.queryParameter(name: String): String? = rawQuery
        ?.split('&')
        ?.firstNotNullOfOrNull { parameter ->
            val separatorIndex = parameter.indexOf('=')
            if (separatorIndex <= 0) return@firstNotNullOfOrNull null

            val parameterName = parameter.take(separatorIndex)
            val parameterValue = parameter.drop(separatorIndex + 1)

            parameterValue.takeIf { parameterName == name && it.isNotBlank() }
        }
}
