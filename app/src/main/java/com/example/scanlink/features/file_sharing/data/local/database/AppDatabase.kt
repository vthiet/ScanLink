package com.example.scanlink.features.file_sharing.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.scanlink.features.file_sharing.data.local.dao.DocumentDao
import com.example.scanlink.features.file_sharing.data.local.entity.DocumentEntity

@Database(
    entities = [DocumentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun documentDao(): DocumentDao
}