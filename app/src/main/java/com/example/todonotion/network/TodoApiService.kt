package com.example.todonotion.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

//https://jeroenmols.com/blog/2023/01/25/development-server-emulator/
//https://dev.to/tusharsadhwani/connecting-android-apps-to-localhost-simplified-57lm
//https://stackoverflow.com/questions/35441481/connection-to-localhost-10-0-2-2-from-android-emulator-timed-out
//https://api.tvmaze.com/
private const val BASE_URL = "https://pixabay.com/api/"

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

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
    suspend fun getPhotos() : Flower

    //https://stackoverflow.com/questions/24100372/retrofit-and-get-using-parameters
    @GET("?key=40521554-653259fd6834861c55e904c4e")
    suspend fun getPhotosWithKey(@Query(value="q", encoded=true) q: String):Flower
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object TodoApi {
    val retrofitService: TodoApiService by lazy { retrofit.create(TodoApiService::class.java) }
}