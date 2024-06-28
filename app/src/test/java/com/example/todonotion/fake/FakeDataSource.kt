package com.example.todonotion.fake

import com.example.todonotion.data.local.token.Token
import com.example.todonotion.data.model.AccessToken
import com.example.todonotion.data.model.AuthResponse
import com.example.todonotion.data.model.Login
import com.example.todonotion.data.model.NestedPost
import com.example.todonotion.data.model.NestedUser
import com.example.todonotion.data.model.Post
import com.example.todonotion.data.model.RefreshToken
import com.example.todonotion.data.model.Signup
import com.example.todonotion.data.model.SignupResponse
import com.example.todonotion.data.model.UpdateNestedPost
import com.example.todonotion.data.model.UpdatePost
import com.example.todonotion.data.model.User

object FakeDataSource {
    const val idOne = "id1"
    const val idTwo = "id2"
    const val idThree = "id3"
    const val titleOne = "mix juice"
    const val titleTwo = "StatisticsViewModelTest"
    const val titleThree = "test is important"
    val testUserName1 = "Ann"
    val testUserName2 = "Bob"

    val contextOne = listOf("content1", "123")
    val contextTwo = listOf("content2", "dfghj")
    val contextThree = listOf("content3", "ffghkk")
    val contentThreeUpdate = listOf("content33", "content33")

    const val createDateOne = "createDate1"
    const val createDateTwo = "createDate2"
    const val createDateThree = "createDate3"
    const val updateDateOne = "2024-01-01"
    const val updateDateTwo = "2024-01-01"
    const val updateDateThree = "2024-01-01"

    val user1 = User(
        todos = listOf(),
        name = "user name",
        email = "test@test.com",
        id = "123434566778",
    )
    val user2 = User(
        todos = listOf(),
        name = "user testName",
        email = "test11@test.com",
        id = "235677777779",
    )

    val testAccessToken = "testAccess14345"

    val testRefreshToken = RefreshToken(
        email = "test@test.com",
        refreshToken = "testreFreshtoken"
    )
    val post1 = Post(
        title = titleOne,
        context = contextOne,
        updateDate = updateDateOne,
        user = user1,
        username = testUserName1
    )

    val post2 = Post(
        title = titleTwo,
        context = contextTwo,
        updateDate = updateDateTwo,
        user = user2,
        username = testUserName2
    )

    val postsList = listOf(
        post1, post2
    )

    val addPost = Post(
        title = titleThree,
        context = contextThree,
        updateDate = updateDateThree,
        user = user1,
        username = testUserName1
    )

    val editPost = Post(
        title = titleThree,
        context = contentThreeUpdate,
        updateDate = updateDateThree,
        user = user1,
        username = testUserName1
    )

    const val idToken = 1233467788222222
    const val accessToken = "accessToken"
    const val refreshToken = "refreshToken"

    val fakeToken = Token(
        id = idToken,
        accessToken = accessToken,
        refreshToken = refreshToken,
        userId = user1.id
    )

    val fakeAuthorization = "fakeAuthorization"

    const val testUsername = "testUser"
    const val testEmail = "test@test.com"
    const val testName = "test name"
    const val testPassword = "testPassword"

    val searchTxt = "mix"

    val searchPosts = listOf(post1)

    val fakeSignup =
        Signup(name = testName, email = testEmail, password = testPassword)

    val fakeLogin = Login(email = testEmail, password = testPassword)

    val signupResponse = SignupResponse(accessToken= testAccessToken, newToken = testRefreshToken, newUser = user1)

    val loginResponse = AuthResponse(accessToken= testAccessToken, newToken = testRefreshToken, user = user1)

    val userResponse = NestedUser(user1)

    val updateTokenResponse = AccessToken(refreshToken)

    val addPostResponse = NestedPost(todo = UpdateNestedPost(title = addPost.title, context = addPost.context, id = addPost.id, user = user1.id, username = addPost.username), user = user1)

    val editPostResponse = UpdatePost(todo = UpdateNestedPost(title = editPost.title, context = editPost.context, id = editPost.id, user = user1.id, username = editPost.username))

   // val logoutResponse = Response<Unit>

    // const val signupMessage = "User registered successfully!."
    // val responseBodySignup = Response()



}
