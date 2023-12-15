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
    //search keyWord
    @NonNull @ColumnInfo(name = "userName")
    val userName: String,
    @NonNull @ColumnInfo(name = "email")
    val email: String,
   // @NonNull @ColumnInfo(name = "todos")
   // val todos: List<Post>,
)