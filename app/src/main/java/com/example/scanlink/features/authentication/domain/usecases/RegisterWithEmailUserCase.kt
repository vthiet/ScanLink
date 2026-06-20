package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val repository: IAuthRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String,
        dateOfBirth: String,
        gender: String
    ): Result<UserEntity> {
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(Exception("Email không hợp lệ"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Mật khẩu phải từ 6 ký tự trở lên"))
        }
        return try {
            repository.registerWithEmail(displayName, dateOfBirth, gender, email, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}