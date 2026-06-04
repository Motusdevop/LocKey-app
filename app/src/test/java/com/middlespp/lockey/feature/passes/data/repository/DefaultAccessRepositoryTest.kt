package com.middlespp.lockey.feature.passes.data.repository

import com.middlespp.lockey.feature.passes.data.local.PassStore
import com.middlespp.lockey.feature.passes.data.remote.AccessApi
import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.model.OnlineOpenResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant

class DefaultAccessRepositoryTest {
    @Test
    fun `verify access returns command on success`() = runTest {
        val repository = repositoryWithResponse(
            status = HttpStatusCode.Accepted,
            body = """
                {
                    "status":"accepted",
                    "lock_id":"studio-a1",
                    "command_id":"command-1"
                }
            """.trimIndent()
        )

        val result = repository.verifyAccess(accessPass(), lockCode = "A1B2C3")

        val sent = assertIs<OnlineOpenResult.CommandSent>(result)
        assertEquals("studio-a1", sent.command.lockId)
        assertEquals("command-1", sent.command.commandId)
    }

    @Test
    fun `verify access maps invalid lock code`() = runTest {
        val repository = repositoryWithResponse(
            status = HttpStatusCode.BadRequest,
            body = "Lock code is invalid or expired"
        )

        val result = repository.verifyAccess(accessPass(), lockCode = "A1B2C3")

        assertIs<OnlineOpenResult.InvalidLockCode>(result)
    }

    @Test
    fun `verify access maps invalid access code`() = runTest {
        val repository = repositoryWithResponse(
            status = HttpStatusCode.BadRequest,
            body = "Access code is invalid"
        )

        val result = repository.verifyAccess(accessPass(), lockCode = "A1B2C3")

        assertIs<OnlineOpenResult.InvalidAccessCode>(result)
    }

    @Test
    fun `verify access maps network error`() = runTest {
        val repository = repositoryWithEngine(
            MockEngine { throw IOException("No connection") }
        )

        val result = repository.verifyAccess(accessPass(), lockCode = "A1B2C3")

        assertIs<OnlineOpenResult.NetworkError>(result)
    }

    private fun repositoryWithResponse(
        status: HttpStatusCode,
        body: String
    ): DefaultAccessRepository = repositoryWithEngine(
        MockEngine {
            respond(
                content = body,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
    )

    private fun repositoryWithEngine(engine: MockEngine): DefaultAccessRepository {
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        return DefaultAccessRepository(
            api = AccessApi(client),
            passStore = FakePassStore()
        )
    }

    private fun accessPass(): AccessPass = AccessPass(
        lockId = "studio-a1",
        accessCode = "ABCDEF1234",
        bookingStartsAt = Instant.parse("2026-04-22T12:00:00Z"),
        bookingEndsAt = Instant.parse("2026-04-22T14:00:00Z")
    )

    private class FakePassStore : PassStore {
        private val passes = mutableMapOf<String, AccessPass>()

        override suspend fun save(pass: AccessPass) {
            passes[pass.lockId] = pass
        }

        override suspend fun getByLockId(lockId: String): AccessPass? = passes[lockId]

        override fun observeAll(): Flow<List<AccessPass>> = flowOf(passes.values.toList())

        override suspend fun delete(lockId: String) {
            passes.remove(lockId)
        }

        override suspend fun setPinned(lockId: String, isPinned: Boolean) = Unit

        override suspend fun updateSortOrder(lockId: String, sortOrder: Int) = Unit
    }
}
