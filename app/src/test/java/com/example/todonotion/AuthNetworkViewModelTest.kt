package com.example.todonotion

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todonotion.fake.FakeDataSource
import com.example.todonotion.fake.FakeNetworkUserRepository
import com.example.todonotion.model.dto.PostDto
import com.example.todonotion.model.*
import com.example.todonotion.overview.auth.AuthNetworkViewModel

import com.example.todonotion.rules.MainCoroutineRule

import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before


//https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-survey#4
//https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/MIGRATION.md
//https://github.com/Kotlin/kotlinx.coroutines/issues/3179
//https://developer.android.com/kotlin/coroutines/test
@ExperimentalCoroutinesApi
class AuthNetworkViewModelTest {

    // Subject under test
    private lateinit var authNetworkViewModel: AuthNetworkViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var networkUserRepository: FakeNetworkUserRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        networkUserRepository = FakeNetworkUserRepository()
        authNetworkViewModel = AuthNetworkViewModel(networkUserRepository)
    }

    @Test
    fun authNetworkViewModel_getPosts_verifyPostsSuccess() =
        runTest {
            assertEquals(
                FakeDataSource.postsList,
                authNetworkViewModel.posts.value
            )
        }

    @Test
    fun authNetworkViewModel_getSearchPosts_verifyPostsSuccess() =
        runTest {
            //  withContext(StandardTestDispatcher(testScheduler)) {
            authNetworkViewModel.getSearchAction(FakeDataSource.searchTxt)

            // delay(100)
            advanceUntilIdle()// Yields to perform the registrations

            assertEquals(
                FakeDataSource.searchPosts,
                authNetworkViewModel.filteredPosts.value
            )
            //   }
        }

    @Test
    fun authNetworkViewModel_addPost_verifyAddPostSuccess() =
        runTest {
            //  withContext(StandardTestDispatcher(testScheduler)) {
            val postDto = PostDto(
                title = FakeDataSource.addPost.title,
                context = FakeDataSource.addPost.context,
                username = FakeDataSource.addPost.username
            )
            authNetworkViewModel.addPostAction(postDto = postDto)

            // delay(100)
            advanceUntilIdle()// Yields to perform the registrations

            assertEquals(
                FakeDataSource.addPostResponse,
                authNetworkViewModel.addPost.value
            )
            //  }
        }


    @Test
    fun authNetworkViewModel_editPost_verifyEditPostSuccess() =
        runTest {
            //  withContext(StandardTestDispatcher(testScheduler)) {
            val postDto = PostDto(
                title = FakeDataSource.editPost.title,
                context = FakeDataSource.editPost.context,
                username = FakeDataSource.editPost.username
            )
            authNetworkViewModel.editPostAction(
                postId = FakeDataSource.editPost.id,
                postDto = postDto
            )

            //delay(100)
            advanceUntilIdle()// Yields to perform the registrations

            assertEquals(
                FakeDataSource.editPostResponse,
                authNetworkViewModel.editPost.value
            )
            //  }
        }


    @Test
    fun authNetworkViewModel_loginUser_verifyLoginSuccess() =
        runTest {
            // withContext(StandardTestDispatcher(testScheduler)) {
            val login = Login(FakeDataSource.fakeLogin.email, FakeDataSource.fakeLogin.password)
            authNetworkViewModel.loginAction(login)

            //delay(200)
            advanceUntilIdle()// Yields to perform the registrations

            assertEquals(
                FakeDataSource.loginResponse,
                authNetworkViewModel.authResponse.value
            )
            // }

        }


    @Test
    fun authNetworkViewModel_signupUser_verifySignupSuccess() =
        runTest {
            // withContext(StandardTestDispatcher(testScheduler)) {
            val signup = Signup(
                name = FakeDataSource.fakeSignup.name,
                email = FakeDataSource.fakeSignup.email,
                password = FakeDataSource.fakeSignup.password
            )
            authNetworkViewModel.signupAction(signup)

            // delay(200)
            advanceUntilIdle()// Yields to perform the registrations

            assertEquals(
                FakeDataSource.signupResponse,
                authNetworkViewModel.signupResponse.value
            )
            // }
        }
}