package com.example.scanlink.features.document_scanner.data.local.database.entities

import androidx.room.TypeConverter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FloatArrayConverter {
    @TypeConverter
    fun toByteArray(floatArray: FloatArray?): ByteArray? {
        if (floatArray == null) return null
        val byteBuffer = ByteBuffer.allocate(floatArray.size * 4)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        for (value in floatArray) {
            byteBuffer.putFloat(value)
        }
        return byteBuffer.array()
    }

    @TypeConverter
    fun toFloatArray(byteArray: ByteArray?): FloatArray? {
        if (byteArray == null) return null
        val floatBuffer = ByteBuffer.wrap(byteArray)
            .order(ByteOrder.LITTLE_ENDIAN)
            .asFloatBuffer()
        val floatArray = FloatArray(floatBuffer.limit())
        floatBuffer.get(floatArray)
        return floatArray
    }
}
