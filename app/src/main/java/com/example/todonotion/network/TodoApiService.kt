package com.example.todonotion.network

import com.example.todonotion.model.Flower
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

    //https://stackoverflow.com/questions/24100372/retrofit-and-get-using-parameters
    @GET("?key=40521554-653259fd6834861c55e904c4e")
    suspend fun getPhotosWithKey(@Query(value = "q", encoded = true) q: String): Flower
}

