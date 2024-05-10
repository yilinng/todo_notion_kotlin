package com.example.todonotion.network
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class Post (
    val id: String,
    val title: String,
    val context: List<String>? = null,
    val updateDate: String? = null,
    val user: User,
    val username: String
)