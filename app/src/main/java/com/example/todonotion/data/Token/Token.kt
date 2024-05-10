package com.example.todonotion.data.Token

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Token (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @NonNull @ColumnInfo(name = "accessToken")
    var accessToken: String,
    @NonNull @ColumnInfo(name = "refreshToken")
    val refreshToken: String,
    @NonNull @ColumnInfo(name = "userId")
    val userId: String
)