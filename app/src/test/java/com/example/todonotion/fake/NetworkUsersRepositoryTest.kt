package com.example.todonotion.fake

import com.example.todonotion.data.NetworkRemoteAuthRepository
import com.example.todonotion.fake.FakeDataSource.fakeLogin
import com.example.todonotion.fake.FakeDataSource.fakeSignup
import kotlinx.coroutines.test.runTest
import org.junit.Test
import junit.framework.TestCase.assertEquals


class NetworkUsersRepositoryTest {
    @Test
    fun networkUsersRepository_getPosts_verifyPostList() =
        runTest {
            val repository = NetworkRemoteAuthRepository(
                userApiService = FakeUserApiService()
            )
            assertEquals(FakeDataSource.postsList, repository.getTodos())
        }

    /*
    @Test
    fun networkUsersRepository_getPost_verifyPost() =
        runTest {
            val repository = NetworkUsersRepository(
                userApiService = FakeUsersApiService()
            )
            assertEquals(FakeDataSource.postsList[0], repository.getPost(idOne))
        }
    */
    @Test
    fun networkUsersRepository_login_verifyToken() =
        runTest {
            val repository = NetworkRemoteAuthRepository(
                userApiService = FakeUserApiService()
            )
            assertEquals(FakeDataSource.loginResponse, repository.loginUser(fakeLogin))
        }

    @Test
    fun networkUsersRepository_signup_verifyToken() =
        runTest {
            val repository = NetworkRemoteAuthRepository(
                userApiService = FakeUserApiService()
            )
            assertEquals(FakeDataSource.signupResponse, repository.signupUser(fakeSignup))
        }
}