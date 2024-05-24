package com.example.todonotion.fake

import com.example.todonotion.data.UsersRepository
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

class FakeNetworkUserRepository: UsersRepository {
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
    override suspend fun loginUser(login: Login): AuthResponse {
        return FakeDataSource.loginResponse
    }

    override suspend fun signupUser(signup: Signup): SignupResponse {
        return FakeDataSource.signupResponse
    }

    override suspend fun getUser(authorization: String): NestedUser {
        return FakeDataSource.userResponse
    }

    override suspend fun updateToken(updateToken: UpdateToken): AccessToken {
        return FakeDataSource.updateTokenResponse
    }

    override suspend fun logoutUser(authorization: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getTodos(): List<Post> {
        return FakeDataSource.postsList
    }

    override suspend fun getTodo(postId: String): Post {
        return FakeDataSource.postsList[0]
    }

    override suspend fun searchTodo(title: String): List<Post> {
        return FakeDataSource.searchPosts
    }

    override suspend fun addTodo(authorization: String, postDto: PostDto): NestedPost {
        return FakeDataSource.addPostResponse
    }

    override suspend fun editTodo(
        postId: String,
        authorization: String,
        postDto: PostDto
    ): UpdatePost {
        return FakeDataSource.editPostResponse
    }

    override suspend fun deleteTodo(postId: String, authorization: String): Response<Unit> {
        TODO("Not yet implemented")
    }
}