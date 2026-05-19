package com.example.scanlink

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class UserRepositoryImpl @Inject constructor(
    // private val apiService: ApiService // (Sẽ tiêm Retrofit vào đây sau)
) : UserRepository {

    override suspend fun getUserData(): Result<User> {
        return try {
            kotlinx.coroutines.delay(1500)
            Result.success(User(id = "101", name = "Vo Van Thiet"))
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    @Module
    @InstallIn(SingletonComponent::class) // Ton tai trong suot vong doi cua App
    abstract class RepositoryModule {

        @Binds
        @Singleton // Đảm bảo chỉ có 1 instance của Repository được tạo ra
        abstract fun bindUserRepository(
            userRepositoryImpl: UserRepositoryImpl
        ) : UserRepository
    }
}