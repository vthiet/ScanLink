package com.example.scanlink.features.document_scanner.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentDao
import com.example.scanlink.features.document_scanner.data.local.database.entities.DocumentEntity
import com.example.scanlink.features.document_scanner.data.local.database.entities.PageEntity

@Database(
    entities = [DocumentEntity::class, PageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}
