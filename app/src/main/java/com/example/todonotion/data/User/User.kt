package com.example.todonotion.data.User

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todonotion.network.Post

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @NonNull @ColumnInfo(name = "name")
    val name: String,
    @NonNull @ColumnInfo(name = "email")
    val email: String,
    //@ColumnInfo(name = "todos")
    //val todos: List<String>? = null,
)