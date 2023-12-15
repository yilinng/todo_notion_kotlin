package com.example.todonotion.network
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class Post (
    val id: String,
    val title: String,
    val content: String,
    val createDate: String,
    val updateDate: String? = null,
    val userId: String
)