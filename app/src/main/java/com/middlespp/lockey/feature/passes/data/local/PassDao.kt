package com.middlespp.lockey.feature.passes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(pass: PassEntity)

    @Query("SELECT * FROM passes WHERE lock_id = :lockId")
    suspend fun getByLockId(lockId: String): PassEntity?

    @Query("SELECT * FROM passes ORDER BY is_pinned DESC, sort_order ASC, booking_starts_at DESC")
    fun observeAll(): Flow<List<PassEntity>>

    @Query("DELETE FROM passes WHERE lock_id = :lockId")
    suspend fun deleteByLockId(lockId: String)

    @Query("UPDATE passes SET is_pinned = :isPinned WHERE lock_id = :lockId")
    suspend fun updatePinned(lockId: String, isPinned: Boolean)

    @Query("UPDATE passes SET sort_order = :sortOrder WHERE lock_id = :lockId")
    suspend fun updateSortOrder(lockId: String, sortOrder: Int)
}
