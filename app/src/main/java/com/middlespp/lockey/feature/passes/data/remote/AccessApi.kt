package com.middlespp.lockey.feature.passes.data.remote

import com.middlespp.lockey.core.network.NetworkConfig
import com.middlespp.lockey.feature.passes.data.remote.dto.VerifyAccessRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AccessApi(
    private val httpClient: HttpClient
) {
    suspend fun verifyAccess(
        lockId: String,
        request: VerifyAccessRequestDto
    ): HttpResponse = httpClient.post(NetworkConfig.BASE_URL + NetworkConfig.verifyAccessPath(lockId)) {
        contentType(ContentType.Application.Json)
        setBody(request)
    }
}
