package com.example.scanlinkbackend.auth;

public record FirebaseUserPrincipal(
        String uid,
        String email,
        String name,
        boolean emailVerified
) {
}
