package com.middlespp.lockey.feature.scanner.domain.parse

import com.middlespp.lockey.core.config.QrCodeConfig
import com.middlespp.lockey.feature.scanner.domain.model.ScannedLockCode
import java.net.URI

class LockQrParser {
    fun parse(value: String): ScannedLockCode? {
        val uri = runCatching { URI(value.trim()) }.getOrNull() ?: return null
        if (!uri.isSupportedLockCodeUrl()) return null

        val path = uri.path.orEmpty()
        val lockId = path.removePrefix(QrCodeConfig.OPEN_PATH_PREFIX).takeIf { it.isNotBlank() }
            ?: return null
        val lockCode = uri.queryParameter(QrCodeConfig.LOCK_CODE_QUERY)?.takeIf { it.isNotBlank() }
            ?: return null

        return ScannedLockCode(
            lockId = lockId,
            lockCode = lockCode
        )
    }

    private fun URI.isSupportedLockCodeUrl(): Boolean =
        scheme == QrCodeConfig.SCHEME &&
            host == QrCodeConfig.HOST &&
            path.orEmpty().startsWith(QrCodeConfig.OPEN_PATH_PREFIX)

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
