package com.example.scanlink.features.document_scanner.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentDao
import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentChunkDao
import com.example.scanlink.features.document_scanner.data.local.database.entities.DocumentEntity
import com.example.scanlink.features.document_scanner.data.local.database.entities.DocumentChunkEntity
import com.example.scanlink.features.document_scanner.data.local.database.entities.PageEntity
import com.example.scanlink.features.document_scanner.data.local.database.entities.FloatArrayConverter

@Database(
    entities = [DocumentEntity::class, PageEntity::class, DocumentChunkEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(FloatArrayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun documentChunkDao(): DocumentChunkDao
}
