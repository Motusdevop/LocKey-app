package com.middlespp.lockey.feature.scanner.domain.parse

import com.middlespp.lockey.core.config.QrCodeConfig
import com.middlespp.lockey.feature.scanner.domain.model.ScannedLockCode
import java.net.URI

class LockQrParser {
    fun parse(value: String): ScannedLockCode? {
        val uri = runCatching { URI(value.trim()) }.getOrNull() ?: return null
        val lockCode = uri.queryParameter(QrCodeConfig.LOCK_CODE_QUERY)?.takeIf { it.isNotBlank() }
            ?: return plainLockCode(value)

        val lockId = uri.lockIdFromPath()

        return ScannedLockCode(
            lockId = lockId.orEmpty(),
            lockCode = lockCode
        )
    }

    private fun URI.lockIdFromPath(): String? {
        val path = path.orEmpty()
        if (scheme !in QrCodeConfig.SCHEMES || host.isNullOrBlank()) return null
        return when {
            path.startsWith(QrCodeConfig.OPEN_PATH_PREFIX) -> path.removePrefix(QrCodeConfig.OPEN_PATH_PREFIX)
            else -> path.substringAfterLast('/')
        }.trim('/').takeIf { it.isNotBlank() }
    }

    private fun plainLockCode(value: String): ScannedLockCode? = value
        .takeIf { it.isNotBlank() && !it.contains("://") && !it.contains('?') }
        ?.let { ScannedLockCode(lockId = "", lockCode = it) }

    private fun URI.queryParameter(name: String): String? = rawQuery
        ?.split('&')
        ?.firstNotNullOfOrNull { parameter ->
            val separatorIndex = parameter.indexOf('=')
            if (separatorIndex <= 0) return@firstNotNullOfOrNull null

            val parameterName = parameter.take(separatorIndex)
            val parameterValue = parameter.drop(separatorIndex + 1)

            parameterValue.takeIf { parameterName == name }
        }
}
