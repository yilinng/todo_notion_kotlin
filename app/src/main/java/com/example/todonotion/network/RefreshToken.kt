package com.example.todonotion.network

import com.squareup.moshi.Json

data class RefreshToken (
    val email: String,
    @Json(name = "refresh_token")
    val refreshToken: String,
    //@Json(name = "expires_at")
    //val expiresAt: String,
)