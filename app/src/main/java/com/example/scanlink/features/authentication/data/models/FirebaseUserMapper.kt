package com.example.scanlink.features.authentication.data.models

import com.google.firebase.auth.FirebaseUser
import com.example.scanlink.features.authentication.domain.entities.UserEntity

fun FirebaseUser.toUserProfile(): UserEntity {
    return UserEntity(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString(),
        createdAt = metadata?.creationTimestamp
    )
}