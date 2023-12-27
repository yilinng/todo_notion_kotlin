package com.example.todonotion.network

import com.squareup.moshi.Json
import java.time.LocalDateTime

/**
 * This data class defines a TodoImg photo which includes an ID, and the image URL.
 * The property names of this data class are used by Moshi to match the names of values in JSON.
 */

data class Todo(

    /*
    val id: String,
    val url: String,
    val name: String,
    val type: String,
    val language: String,
    val genres: List<String>,
    val image: ShowImg,
    val rating: Rating,

    val status: String,
    val runtime: Int,
    val averageRuntime: Int,
    val premiered: String,
    val ended: String,
    val officeSite: String,
    val schedule: Schedule,
    val rating: Rating,
    val weight: Int,
    val network: Network,
    val externals: Externals,
    val image:ShowImg,
    val summary: String,
    val _links: ShowLink,
    val previousepisode: Previousepisode
    */

    /*
    val id: String,
    val title: String,
    val content: String,
    val createDate: LocalDateTime,
    val updateDate: LocalDateTime,
    val userId: String
    */
    val id: String,
    val pageURL: String,
    val type: String,
    val tags: String,
    val views: Int,
    val downloads: Int,
    val collections: Int,
    val likes: Int,
    val comments: Int,
    val user_id: Int,
    val user: String,
    val userImageURL: String,
    @Json(name = "webformatURL") val imgSrcUrl: String,


)