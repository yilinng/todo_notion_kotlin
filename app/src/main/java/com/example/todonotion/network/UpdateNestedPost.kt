package com.example.todonotion.network

data class UpdateNestedPost (
    val id: String,
    val title: String,
    val context: List<String>? = null,
    val updateDate: String? = null,
    val user: String,
    val username: String
)
