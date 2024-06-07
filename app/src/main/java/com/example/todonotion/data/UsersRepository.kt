package com.example.todonotion.data

import com.example.todonotion.model.*
import com.example.todonotion.model.dto.PostDto
import com.example.todonotion.network.UserApiService
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject


interface UsersRepository {

    suspend fun loginUser(@Body login: Login): AuthResponse

    suspend fun signupUser(@Body signup: Signup): SignupResponse

    suspend fun getUser(@Header("Authorization") authorization: String): NestedUser

    suspend fun updateToken(@Body updateToken: UpdateToken): AccessToken

    suspend fun logoutUser(
        @Header("Authorization") authorization: String
    ): Response<Unit>

    suspend fun getTodos(): List<Post>

    suspend fun getTodo(@Path("id") postId: String): UpdatePost

    suspend fun searchTodo(@Path("keyword") title: String): List<Post>

    suspend fun addTodo(
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): NestedPost

    suspend fun editTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): UpdatePost

    //https://stackoverflow.com/questions/59636219/how-to-handle-204-response-in-retrofit-using-kotlin-coroutines
    //https://stackoverflow.com/questions/37942474/delete-method-is-not-supportingnon-body-http-method-cannot-contain-body-or-t

    suspend fun deleteTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>
}

class NetworkUsersRepository @Inject constructor(
    private val userApiService: UserApiService
) : UsersRepository {
    override suspend fun loginUser(login: Login): AuthResponse = userApiService.loginUser(login)

    override suspend fun signupUser(signup: Signup): SignupResponse =
        userApiService.signupUser(signup)

    override suspend fun getUser(authorization: String): NestedUser =
        userApiService.getUser(authorization)

    override suspend fun updateToken(updateToken: UpdateToken): AccessToken =
        userApiService.updateToken(updateToken)

    override suspend fun logoutUser(authorization: String): Response<Unit> =
        userApiService.logoutUser(authorization)

    override suspend fun getTodos(): List<Post> = userApiService.getTodos()

    override suspend fun getTodo(postId: String): UpdatePost = userApiService.getTodo(postId)

    override suspend fun searchTodo(title: String): List<Post> = userApiService.searchTodo(title)

    override suspend fun addTodo(authorization: String, postDto: PostDto): NestedPost =
        userApiService.addTodo(authorization, postDto)

    override suspend fun editTodo(
        postId: String,
        authorization: String,
        postDto: PostDto
    ): UpdatePost = userApiService.editTodo(postId, authorization, postDto)

    override suspend fun deleteTodo(postId: String, authorization: String): Response<Unit> =
        userApiService.deleteTodo(postId, authorization)

}