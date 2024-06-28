package com.example.todonotion.fake

import com.example.todonotion.data.model.AccessToken
import com.example.todonotion.data.model.AuthResponse
import com.example.todonotion.data.model.Login
import com.example.todonotion.data.model.NestedPost
import com.example.todonotion.data.model.NestedUser
import com.example.todonotion.data.model.Post
import com.example.todonotion.data.model.Signup
import com.example.todonotion.data.model.SignupResponse
import com.example.todonotion.data.model.UpdatePost
import com.example.todonotion.data.model.UpdateToken
import com.example.todonotion.data.model.dto.PostDto
import com.example.todonotion.data.network.UserApiService
import retrofit2.Response

class FakeUserApiService: UserApiService {
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