package com.example.scanlink.features.authentication.data.repositories

import com.example.scanlink.core.exceptions.AccountNotSyncedException
import com.example.scanlink.core.exceptions.BackendUnauthorizedException
import com.example.scanlink.core.exceptions.BadRequestException
import com.example.scanlink.core.exceptions.EmailAlreadyExistsException
import com.example.scanlink.core.exceptions.ForbiddenException
import com.example.scanlink.core.exceptions.InvalidServerResponseException
import com.example.scanlink.core.exceptions.ServerErrorException
import com.example.scanlink.features.authentication.data.datasources.remote.api.IAuthApiService
import com.example.scanlink.features.authentication.data.datasources.remote.dto.ApiResponse
import com.example.scanlink.features.authentication.data.datasources.remote.dto.RegisterResponse
import com.example.scanlink.features.authentication.data.datasources.remote.dto.toUserEntity
import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authApiService: IAuthApiService
) : IAuthenticationRepository {

    // ── Đọc message lỗi từ error body của Response<T> ─────────────────────────
    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val json = response.errorBody()?.string() ?: return "Lỗi không xác định từ server"
            val type = object : TypeToken<ApiResponse<Nothing>>() {}.type
            val apiError = Gson().fromJson<ApiResponse<Nothing>>(json, type)
            apiError.message.ifBlank { "Lỗi không xác định từ server" }
        } catch (_: Exception) {
            "Lỗi không xác định từ server"
        }
    }

    // ── Ánh xạ HTTP status code → custom exception (SDD mục 5.2) ────────────
    private fun mapHttpError(response: Response<*>): Exception {
        val msg = parseErrorMessage(response)
        return when (response.code()) {
            400  -> BadRequestException(msg)
            401  -> BackendUnauthorizedException(msg)
            403  -> ForbiddenException(msg)
            404  -> AccountNotSyncedException(msg)   // SDD INT-API-002: chưa sync backend
            409  -> EmailAlreadyExistsException(msg) // Email đã đăng ký trên backend
            in 500..599 -> ServerErrorException(msg)
            else -> Exception("Lỗi server (${response.code()}): $msg")
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    override suspend fun registerWithEmailAndPassword(
        displayName: String,
        email: String,
        password: String
    ): Result<UserEntity> = runCatching {
        // 1. Tạo tài khoản trên Firebase Authentication
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Không thể tạo tài khoản Firebase")

        // 2. Cập nhật Display Name trên Firebase profile
        val profileUpdates = userProfileChangeRequest { this.displayName = displayName }
        firebaseUser.updateProfile(profileUpdates).await()

        // 3. Lấy ID Token mới (forceRefresh = true)
        val idToken = firebaseUser.getIdToken(true).await().token
            ?: throw Exception("Lỗi không lấy được Token xác thực")

        // 4. POST /api/v1/auth/register (SDD INT-API-001)
        val response: Response<ApiResponse<RegisterResponse>> =
            authApiService.registerWithEmailAndPassword(authorization = "Bearer $idToken")

        if (!response.isSuccessful) throw mapHttpError(response)

        response.body()?.data?.toUserEntity() ?: throw InvalidServerResponseException()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Result<UserEntity> = runCatching {
        // 1. Đăng nhập Firebase Authentication
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Đăng nhập Firebase thất bại")

        // 2. Lấy ID Token mới nhất (forceRefresh = true)
        val idToken = firebaseUser.getIdToken(true).await().token
            ?: throw Exception("Lỗi không lấy được Token xác thực")

        // 3. POST /api/v1/auth/login (SDD INT-API-002)
        val response: Response<ApiResponse<RegisterResponse>> =
            authApiService.loginWithEmail(authorization = "Bearer $idToken")

        if (!response.isSuccessful) throw mapHttpError(response)

        response.body()?.data?.toUserEntity() ?: throw InvalidServerResponseException()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    override suspend fun logout(): Result<Unit> = runCatching { firebaseAuth.signOut() }

    override suspend fun isLoggedIn(): Result<Boolean> = runCatching {
        firebaseAuth.currentUser != null
    }

    override suspend fun getCurrentUser(): Result<UserEntity> = runCatching {
        val firebaseUser = firebaseAuth.currentUser
            ?: throw Exception("Không có người dùng đang đăng nhập")

        // forceRefresh = false: dùng token cached, tránh gọi Firebase thêm
        val idToken = firebaseUser.getIdToken(false).await().token
            ?: throw Exception("Lỗi không lấy được Token xác thực")

        val response: Response<ApiResponse<RegisterResponse>> =
            authApiService.loginWithEmail(authorization = "Bearer $idToken")

        if (!response.isSuccessful) throw mapHttpError(response)

        response.body()?.data?.toUserEntity() ?: throw InvalidServerResponseException()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override suspend fun getIdToken(): Result<String> = runCatching {
        val firebaseUser = firebaseAuth.currentUser
            ?: throw Exception("Không có người dùng đang đăng nhập")
        firebaseUser.getIdToken(true).await().token
            ?: throw Exception("Lỗi không lấy được Token xác thực")
    }
}

