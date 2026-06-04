package com.middlespp.lockey.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.middlespp.lockey.feature.passes.data.local.PassDao
import com.middlespp.lockey.feature.passes.data.local.PassEntity

@Database(
    entities = [PassEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun passDao(): PassDao
}
