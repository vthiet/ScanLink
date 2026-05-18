package com.example.scanlink

data class User (
    val id: String,
    val name: String
)

interface UserRepository {
    suspend fun getUserData(): Result<User>
}

class GetUserUseCase(private val userRepository: UserRepository){
    suspend operator fun invoke(): Result<User> {
        return userRepository.getUserData();
    }
}