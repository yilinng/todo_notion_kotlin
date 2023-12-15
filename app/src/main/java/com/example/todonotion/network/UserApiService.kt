package com.example.todonotion.network

import com.example.todonotion.network.Login
import com.example.todonotion.network.Signup
import com.example.todonotion.data.Token.Token
import com.example.todonotion.network.dto.PostDto

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


//https://jeroenmols.com/blog/2023/01/25/development-server-emulator/
//https://dev.to/tusharsadhwani/connecting-android-apps-to-localhost-simplified-57lm
//https://stackoverflow.com/questions/35441481/connection-to-localhost-10-0-2-2-from-android-emulator-timed-out
//https://api.tvmaze.com/
private const val BASE_URL = "http://10.0.2.2:1717/api/"


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

//https://stackoverflow.com/questions/14366001/dto-and-dao-concepts-and-mvc
//https://stackoverflow.com/questions/41078866/retrofit2-authorization-global-interceptor-for-access-token
//http://localhost:1717/api/todos/search/?title="test"
interface UserApiService {

    @POST("auth/login")
    suspend fun loginUser(@Body login: Login): Token

    @POST("auth/signup")
    suspend fun signupUser(@Body signup: Signup): ResponseBody

    @GET("auth/user")
    suspend fun getUser(@Header("Authorization") authorization: String): User

    @POST("auth/logout")
    suspend fun logoutUser(
        @Header("Authorization") authorization: String,
        @Body token: String
    ): ResponseBody

    @GET("todos")
    suspend fun getTodos(): List<Post>

    @GET("todos/{id}")
    suspend fun getTodo(@Path("id") postId: String): Post

    @GET("todos/search/")
    suspend fun searchTodo(@Query(value = "title", encoded = true) title: String): List<Post>

    @POST("todos")
    suspend fun addTodo(
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    @PATCH("todos/{id}")
    suspend fun editTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    @DELETE("todos/{id}")
    suspend fun deleteTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String
    ): ResponseBody

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object UserApi {
    val retrofitService: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
}