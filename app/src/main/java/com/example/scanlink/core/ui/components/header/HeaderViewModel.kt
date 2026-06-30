package com.example.scanlink.core.ui.components.header

import androidx.lifecycle.ViewModel
import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HeaderState(
    val user: UserEntity? = null
)

@HiltViewModel
class HeaderViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(
        HeaderState(user = firebaseAuth.currentUser.toUserEntity())
    )
    val state: StateFlow<HeaderState> = _state.asStateFlow()

    fun refresh() {
        _state.update {
            it.copy(user = firebaseAuth.currentUser.toUserEntity())
        }
    }

    private fun com.google.firebase.auth.FirebaseUser?.toUserEntity(): UserEntity? {
        return this?.let { firebaseUser ->
            UserEntity(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString(),
                isEmailVerified = firebaseUser.isEmailVerified,
                providerId = firebaseUser.providerData
                    .firstOrNull { it.providerId != FirebaseAuthProviderId }
                    ?.providerId
            )
        }
    }

    private companion object {
        const val FirebaseAuthProviderId = "firebase"
    }
}
