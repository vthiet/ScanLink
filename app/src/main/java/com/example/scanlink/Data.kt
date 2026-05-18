package com.example.scanlink

class UserRepositoryImpl : UserRepository {

    override suspend fun getUserData(): Result<User> {
        return try {
            kotlinx.coroutines.delay(1500)
            Result.success(User(id = "101", name = "Vo Van Thiet"))
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}