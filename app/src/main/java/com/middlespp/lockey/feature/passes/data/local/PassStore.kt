package com.middlespp.lockey.feature.passes.data.local

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

interface PassStore {
    suspend fun save(pass: AccessPass)
    suspend fun getByLockId(lockId: String): AccessPass?
    fun observeAll(): Flow<List<AccessPass>>
    suspend fun delete(lockId: String)
    suspend fun setPinned(lockId: String, isPinned: Boolean)
    suspend fun updateSortOrder(lockId: String, sortOrder: Int)
}

class RoomPassStore(
    private val dao: PassDao
) : PassStore {
    override suspend fun save(pass: AccessPass) {
        dao.save(pass.toEntity())
    }

    override suspend fun getByLockId(lockId: String): AccessPass? = dao.getByLockId(lockId)?.toDomain()

    override fun observeAll(): Flow<List<AccessPass>> = dao.observeAll().map { passes ->
        passes.map { it.toDomain() }
    }

    override suspend fun delete(lockId: String) {
        dao.deleteByLockId(lockId)
    }

    override suspend fun setPinned(lockId: String, isPinned: Boolean) {
        dao.updatePinned(lockId, isPinned)
    }

    override suspend fun updateSortOrder(lockId: String, sortOrder: Int) {
        dao.updateSortOrder(lockId, sortOrder)
    }
}

private fun AccessPass.toEntity(): PassEntity = PassEntity(
    lockId = lockId,
    accessCode = accessCode,
    bookingStartsAt = bookingStartsAt.toString(),
    bookingEndsAt = bookingEndsAt.toString(),
    isPinned = isPinned,
    sortOrder = sortOrder
)

private fun PassEntity.toDomain(): AccessPass = AccessPass(
    lockId = lockId,
    accessCode = accessCode,
    bookingStartsAt = Instant.parse(bookingStartsAt),
    bookingEndsAt = Instant.parse(bookingEndsAt),
    isPinned = isPinned,
    sortOrder = sortOrder
)
