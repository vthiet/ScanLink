package com.example.scanlink.features.authentication.data.repositories

import com.example.scanlink.features.authentication.data.datasources.remote.api.IAuthApiService
import com.example.scanlink.features.authentication.data.datasources.remote.dto.RegisterRequest
import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authApiService: IAuthApiService
) : IAuthRepository {

    override suspend fun registerWithEmail(
        displayName: String,
        dateOfBirth: String,
        gender: String,
        email: String,
        password: String
    ): Result<UserEntity> = runCatching {
        // runCatching <-> try-catch, tự động bọc kết quả vào Result.success or Result.failure

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

        val registerRequest = RegisterRequest(
            email = email,
            displayName = displayName,
            dateOfBirth = dateOfBirth,
            gender = gender
        )

        // Gọi API Spring Boot
        // Gửi token lên header: "Bearer $idToken"
        // (Thực tế bạn nên cấu hình AuthInterceptor trong OkHttp để nó tự đính kèm Token,
        // ở đây mình viết rõ ra để bạn dễ hình dung luồng đi)
        val registerResponse = authApiService.registerWithEmail(
            authorization = "Bearer $idToken",
            request = registerRequest
        )

        UserEntity(
            uid = registerResponse.uid,
            email = registerResponse.email,
            displayName = registerResponse.displayName,
            dateOfBirth = registerResponse.dateOfBirth,
            gender = registerResponse.gender
        )
    }

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<UserEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun isLoggedIn(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUser(): Result<UserEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getIdToken(): Result<String> {
        TODO("Not yet implemented")
    }


}
