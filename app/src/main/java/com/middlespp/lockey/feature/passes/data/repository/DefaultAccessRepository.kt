package com.middlespp.lockey.feature.passes.data.repository

import com.middlespp.lockey.feature.passes.data.local.PassStore
import com.middlespp.lockey.feature.passes.data.remote.AccessApi
import com.middlespp.lockey.feature.passes.data.remote.dto.VerifyAccessRequestDto
import com.middlespp.lockey.feature.passes.data.remote.dto.VerifyAccessResponseDto
import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.model.OnlineOpenResult
import com.middlespp.lockey.feature.passes.domain.model.OpenCommand
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import java.io.IOException
import kotlinx.coroutines.flow.Flow

class DefaultAccessRepository(
    private val api: AccessApi,
    private val passStore: PassStore
) : AccessRepository {
    override suspend fun savePass(pass: AccessPass) {
        passStore.save(pass)
    }

    override suspend fun getPass(lockId: String): AccessPass? = passStore.getByLockId(lockId)

    override fun observePasses(): Flow<List<AccessPass>> = passStore.observeAll()

    override suspend fun deletePass(lockId: String) {
        passStore.delete(lockId)
    }

    override suspend fun setPassPinned(lockId: String, isPinned: Boolean) {
        passStore.setPinned(lockId, isPinned)
    }

    override suspend fun updatePassOrder(lockId: String, sortOrder: Int) {
        passStore.updateSortOrder(lockId, sortOrder)
    }

    override suspend fun verifyAccess(
        pass: AccessPass,
        lockCode: String
    ): OnlineOpenResult {
        val request = VerifyAccessRequestDto(
            accessCode = pass.accessCode,
            lockCode = lockCode,
            bookingStartsAt = pass.bookingStartsAt.toString(),
            bookingEndsAt = pass.bookingEndsAt.toString()
        )

        return try {
            val response = api.verifyAccess(pass.lockId, request)
            when (response.status) {
                HttpStatusCode.Accepted -> response.toCommandSentResult()
                HttpStatusCode.BadRequest -> response.toBadRequestResult()
                HttpStatusCode.Forbidden -> OnlineOpenResult.BookingWindowClosed
                HttpStatusCode.NotFound -> OnlineOpenResult.NetworkError
                HttpStatusCode.UnprocessableEntity -> OnlineOpenResult.InvalidRequest
                else -> OnlineOpenResult.UnknownError
            }
        } catch (_: IOException) {
            OnlineOpenResult.NetworkError
        } catch (_: Exception) {
            OnlineOpenResult.UnknownError
        }
    }

    private suspend fun HttpResponse.toCommandSentResult(): OnlineOpenResult {
        val body = body<VerifyAccessResponseDto>()
        return OnlineOpenResult.CommandSent(
            command = OpenCommand(
                lockId = body.lockId,
                commandId = body.commandId
            )
        )
    }

    private suspend fun HttpResponse.toBadRequestResult(): OnlineOpenResult {
        val errorBody = bodyAsText()
        return when {
            errorBody.contains(LOCK_CODE_ERROR, ignoreCase = true) -> OnlineOpenResult.InvalidLockCode
            errorBody.contains(ACCESS_CODE_ERROR, ignoreCase = true) -> OnlineOpenResult.InvalidAccessCode
            else -> OnlineOpenResult.InvalidRequest
        }
    }

    private companion object {
        const val LOCK_CODE_ERROR = "Lock code is invalid or expired"
        const val ACCESS_CODE_ERROR = "Access code is invalid"
    }
}
