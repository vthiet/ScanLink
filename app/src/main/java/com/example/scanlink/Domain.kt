package com.example.scanlink

import javax.inject.Inject

data class User (
    val id: String,
    val name: String
)

interface UserRepository {
    suspend fun getUserData(): Result<User>
}

// @Inject constructor để Hilt biết cách khởi tạo class này
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(): Result<User> {
        return userRepository.getUserData();
    }
}