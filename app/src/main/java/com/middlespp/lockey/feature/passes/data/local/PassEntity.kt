package com.middlespp.lockey.feature.passes.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passes")
data class PassEntity(
    @PrimaryKey
    @ColumnInfo(name = "lock_id")
    val lockId: String,
    @ColumnInfo(name = "access_code")
    val accessCode: String,
    @ColumnInfo(name = "booking_starts_at")
    val bookingStartsAt: String,
    @ColumnInfo(name = "booking_ends_at")
    val bookingEndsAt: String,
    @ColumnInfo(name = "is_pinned", defaultValue = "0")
    val isPinned: Boolean,
    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int
)
