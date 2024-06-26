package com.example.todonotion.data.user

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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