package com.example.scanlink.features.authentication.data.repositories

import com.example.scanlink.features.authentication.data.datasources.remote.api.IAuthApiService
import com.example.scanlink.features.authentication.data.datasources.remote.dto.ApiResponse
import com.example.scanlink.features.authentication.data.datasources.remote.dto.RegisterResponse
import com.example.scanlink.features.authentication.data.datasources.remote.dto.toUserEntity
import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authApiService: IAuthApiService
) : IAuthenticationRepository {

    override suspend fun registerWithEmailAndPassword(
        displayName: String,
        email: String,
        password: String
    ): Result<UserEntity> = runCatching {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Không thể tạo tài khoản Firebase")

        val profileUpdates = userProfileChangeRequest {
            this.displayName = displayName
        }
        firebaseUser.updateProfile(profileUpdates).await()

        // Lấy ID Token (Mã thông báo bảo mật) từ Firebase
        // true = forceRefresh, ép Firebase cấp một token mới
        val tokenResult = firebaseUser.getIdToken(true).await()
        val idToken = tokenResult.token ?: throw Exception("Lỗi không lấy được Token xác thực")
        // Gọi API Spring Boot
        // Gửi token lên header: "Bearer $idToken"
        // (Thực tế bạn nên cấu hình AuthInterceptor trong OkHttp để nó tự đính kèm Token,
        // ở đây mình viết rõ ra để bạn dễ hình dung luồng đi)
        val response: ApiResponse<RegisterResponse> = authApiService.registerWithEmailAndPassword(
            authorization = "Bearer $idToken"
        )

        response.data?.toUserEntity() ?: throw Exception("Server trả về dữ liệu không hợp lệ")
    }

    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Result<UserEntity> = runCatching {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Đăng nhập Firebase thất bại")

        val tokenResult = firebaseUser.getIdToken(true).await()
        val idToken = tokenResult.token ?: throw Exception("Lỗi không lấy được Token xác thực")

        val response: ApiResponse<RegisterResponse> = authApiService.loginWithEmail(
            authorization = "Bearer $idToken"
        )

        response.data?.toUserEntity() ?: throw Exception("Server trả về dữ liệu không hợp lệ")
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        firebaseAuth.signOut()
    }

    override suspend fun isLoggedIn(): Result<Boolean> = runCatching {
        firebaseAuth.currentUser != null
    }

    override suspend fun getCurrentUser(): Result<UserEntity> = runCatching {
        val firebaseUser = firebaseAuth.currentUser
            ?: throw Exception("Không có người dùng đang đăng nhập")

        val tokenResult = firebaseUser.getIdToken(false).await()
        val idToken = tokenResult.token ?: throw Exception("Lỗi không lấy được Token xác thực")

        val response: ApiResponse<RegisterResponse> = authApiService.loginWithEmail(
            authorization = "Bearer $idToken"
        )
        response.data?.toUserEntity() ?: throw Exception("Server trả về dữ liệu không hợp lệ")
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override suspend fun getIdToken(): Result<String> = runCatching {
        val firebaseUser = firebaseAuth.currentUser
            ?: throw Exception("Không có người dùng đang đăng nhập")
        val tokenResult = firebaseUser.getIdToken(true).await()
        tokenResult.token ?: throw Exception("Lỗi không lấy được Token xác thực")
    }


}
