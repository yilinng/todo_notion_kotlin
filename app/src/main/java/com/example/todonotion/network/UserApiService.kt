package com.example.todonotion.network

import com.example.todonotion.network.dto.PostDto

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


//https://jeroenmols.com/blog/2023/01/25/development-server-emulator/
//https://dev.to/tusharsadhwani/connecting-android-apps-to-localhost-simplified-57lm
//https://stackoverflow.com/questions/35441481/connection-to-localhost-10-0-2-2-from-android-emulator-timed-out
//https://api.tvmaze.com/
private const val BASE_URL = "https://express-api-react-notion.vercel.app/api/"


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

    @POST("users/login")
    suspend fun loginUser(@Body login: Login): AuthResponse

    @POST("users/signup")
    suspend fun signupUser(@Body signup: Signup): AuthResponse

    @GET("users")
    suspend fun getUser(@Header("Authorization") authorization: String): NestedUser

    @POST("users/token")
    suspend fun updateToken(@Body updateToken: UpdateToken): AccessToken

    //https://stackoverflow.com/questions/59636219/how-to-handle-204-response-in-retrofit-using-kotlin-coroutines
    //https://stackoverflow.com/questions/37942474/delete-method-is-not-supportingnon-body-http-method-cannot-contain-body-or-t
    @HTTP(method = "DELETE", path = "users/logout", hasBody = true)
    suspend fun logoutUser(
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @GET("todos")
    suspend fun getTodos(): List<Post>

    @GET("todos/{id}")
    suspend fun getTodo(@Path("id") postId: String): Post


    @GET("todos/{keyword}")
    suspend fun searchTodo(@Path("keyword") title: String): List<Post>

    @POST("todos")
    suspend fun addTodo(
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): NestedPost

    @PATCH("todos/{id}")
    suspend fun editTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): UpdatePost

    //https://stackoverflow.com/questions/59636219/how-to-handle-204-response-in-retrofit-using-kotlin-coroutines
    //https://stackoverflow.com/questions/37942474/delete-method-is-not-supportingnon-body-http-method-cannot-contain-body-or-t
    @HTTP(method = "DELETE", path = "todos/{id}", hasBody = false)
    suspend fun deleteTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object UserApi {
    val retrofitService: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
}