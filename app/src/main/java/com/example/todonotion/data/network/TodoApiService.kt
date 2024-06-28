package com.example.todonotion.data.network

import com.example.todonotion.data.model.Flower
import com.example.todonotion.data.model.Todo
import retrofit2.http.*

/**
 * A public interface that exposes the [getPhotos] method
 * https://stackoverflow.com/questions/32942661/how-can-retrofit-2-0-parse-nested-json-object
 */
interface TodoApiService {

    /**
     * Returns a [List] of [Todo] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     *?key=40521554-653259fd6834861c55e904c4e
     */

    @GET("?key=40521554-653259fd6834861c55e904c4e")
    suspend fun getPhotos(): Flower

    @GET("?key=40521554-653259fd6834861c55e904c4e")
    suspend fun getPhotosById(@Query(value = "id", encoded = true) id: String): Flower

    //https://stackoverflow.com/questions/24100372/retrofit-and-get-using-parameters
    @GET("?key=40521554-653259fd6834861c55e904c4e")
    suspend fun getPhotosWithKey(@Query(value = "q", encoded = true) q: String): Flower
}

