package com.example.todonotion.network.dto


data class PostDto (
    private val title: String,
    private val context: List<String>? = null,
    private val username: String
)