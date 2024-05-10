package com.example.todonotion.network

data class AuthResponse (
    val accessToken: String,
    val newToken: RefreshToken,
    val user: User
)