package com.example.scanlink.features.authentication.data.datasources

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun register(
        email: String,
        password: String,
        displayName: String? = null
    ): FirebaseUser {
        val result = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()

        val user = result.user ?: throw IllegalStateException("Register failed")

        if (displayName != null) {
            val profileUpdates = userProfileChangeRequest {
                this.displayName = displayName
            }
            user.updateProfile(profileUpdates).await()
        }
        return user
    }

    suspend fun login(email: String, password: String): FirebaseUser {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()

        return result.user ?: throw IllegalStateException("Login failed")
    }

    suspend fun getIdToken(firebaseUser: FirebaseUser): String {
        val result = firebaseUser.getIdToken(true).await()
        return result.token ?: throw IllegalStateException("Failed to get Firebase ID token")
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}
