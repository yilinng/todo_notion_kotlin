package com.example.todonotion.network

import com.example.todonotion.model.AccessToken
import com.example.todonotion.model.AuthResponse
import com.example.todonotion.model.Login
import com.example.todonotion.model.NestedPost
import com.example.todonotion.model.NestedUser
import com.example.todonotion.model.Post
import com.example.todonotion.model.Signup
import com.example.todonotion.model.SignupResponse
import com.example.todonotion.model.UpdatePost
import com.example.todonotion.model.UpdateToken
import com.example.todonotion.model.dto.PostDto

import retrofit2.Response
import retrofit2.http.*

//https://stackoverflow.com/questions/14366001/dto-and-dao-concepts-and-mvc
//https://stackoverflow.com/questions/41078866/retrofit2-authorization-global-interceptor-for-access-token
//http://localhost:1717/api/todos/search/?title="test"
interface UserApiService {

    @POST("users/login")
    suspend fun loginUser(@Body login: Login): AuthResponse

    @POST("users/signup")
    suspend fun signupUser(@Body signup: Signup): SignupResponse

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
    suspend fun getTodo(@Path("id") postId: String): UpdatePost


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
