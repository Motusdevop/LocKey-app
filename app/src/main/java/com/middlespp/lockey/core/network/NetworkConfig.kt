package com.middlespp.lockey.core.network

object NetworkConfig {
    const val BASE_URL = "http://45.154.35.214/LocKey"
    const val API_PREFIX = "/api/v1"

    fun verifyAccessPath(lockId: String): String = "$API_PREFIX/locks/$lockId/verify-access"
}
