package com.example.todonotion.network

import com.squareup.moshi.Json

data class User (
    val todos: List<String>? = null,
    val name: String,
    val email: String,
    val id: String,
)