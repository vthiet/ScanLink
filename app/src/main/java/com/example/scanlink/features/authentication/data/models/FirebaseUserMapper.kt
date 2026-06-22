package com.example.scanlink.features.authentication.data.models

import com.google.firebase.auth.FirebaseUser
import com.example.scanlink.features.authentication.domain.entities.UserEntity

fun FirebaseUser.toUserProfile(): UserEntity {
    return UserEntity(
        uid = uid,
        email = email,
        displayName = displayName,
        phoneNumber = phoneNumber,
        photoUrl = photoUrl?.toString(),
        isEmailVerified = isEmailVerified,
        providerId = providerId,
        createdAt = metadata?.creationTimestamp,
        updatedAt = metadata?.lastSignInTimestamp
    )
}
