package com.example.scanlink.features.authentication.data.datasources.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class UserDto(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val dateOfBirth: String?,
    val gender: String?,
    val role: String?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt   : Long
)

class AuthRemoteDataSource(
    private val baseUrl: String
) {
    suspend fun registerToSpringBoot(
        idToken: String,
        displayName: String?,
        dateOfBirth: String?,
        gender: String?
    ): UserDto = withContext(Dispatchers.IO) {
        val requestBody = JSONObject()
            .put("displayName", displayName)
            .put("dateOfBirth", dateOfBirth)
            .put("gender", gender)
            .toString()

        val connection = (URL("$baseUrl/api/v1/auth/register").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 10_000
            doOutput = true
            setRequestProperty("Authorization", "Bearer $idToken")
            setRequestProperty("Content-Type", "application/json")
        }

        try {
            connection.outputStream.use { it.write(requestBody.toByteArray()) }
            val statusCode = connection.responseCode
            if (statusCode !in 200..299) {
                throw IOException("Register backend failed with status $statusCode")
            }

            val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(responseBody)
            fun nullableString(key: String): String? {
                return if (json.isNull(key)) null else json.getString(key)
            }

            UserDto(
                uid = json.getString("uid"),
                email = nullableString("email"),
                displayName = nullableString("displayName"),
                dateOfBirth = nullableString("dateOfBirth"),
                gender = nullableString("gender"),
                role = nullableString("role"),
                isActive = json.optBoolean("active", true),
                createdAt = json.optLong("createdAt", 0L),
                updatedAt = json.optLong("updatedAt", 0L)
            )
        } finally {
            connection.disconnect()
        }
    }
}
