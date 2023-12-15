package com.example.todonotion.network

//import android.provider.ContactsContract.CommonDataKinds.Email

data class User (
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val todos: List<Post>,

)