package com.middlespp.lockey.feature.passes.domain.repository

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.model.OnlineOpenResult
import kotlinx.coroutines.flow.Flow

interface AccessRepository {
    suspend fun savePass(pass: AccessPass)

    suspend fun getPass(lockId: String): AccessPass?

    fun observePasses(): Flow<List<AccessPass>>

    suspend fun deletePass(lockId: String)

    suspend fun setPassPinned(lockId: String, isPinned: Boolean)

    suspend fun updatePassOrder(lockId: String, sortOrder: Int)

    suspend fun verifyAccess(
        pass: AccessPass,
        lockCode: String
    ): OnlineOpenResult
}
