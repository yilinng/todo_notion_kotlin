package com.example.todonotion.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.todonotion.data.Token.Token
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*

import org.junit.runner.RunWith
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*

//https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-survey#6
//https://developers.google.com/learn/pathways/firebase-android-jetpack
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TokensDaoTest {

    private lateinit var database: KeyDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            KeyDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTokenAndGetById() = runTest {
        // GIVEN - Insert a task.
        val token = Token(id= 5464646, accessToken = "testaccessToken", refreshToken = "testRefreshToken", userId = "wefffhhh")
        database.tokenDao().insert(token = token)

        // WHEN - Get the task by id from the database.
        val loaded = database.tokenDao().getToken(token.id.toInt())

        // THEN - The loaded data contains the expected values.
        TestCase.assertEquals(notNullValue(), loaded as Token)
        TestCase.assertEquals(token.id, loaded.id)
        TestCase.assertEquals(token.accessToken, loaded.accessToken)
        TestCase.assertEquals(token.refreshToken, loaded.refreshToken)
        TestCase.assertEquals(token.userId, loaded.userId)
    }

    /*
    @Test
    fun deleteTokenAndGetById() = runTest {
        // GIVEN - Insert a task.
        val token = Token(id= 5464646, accessToken = "testaccessToken", refreshToken = "testRefreshToken", userId = "wefffhhh")
        database.tokenDao().insert(token = token)

        // When the token is deleted
        database.tokenDao().delete(token)
        val loaded = database.tokenDao().getTokens()

        // THEN - The loaded data contains the expected values.
        TestCase.assertEquals(nullValue(), loaded as Token)
       // TestCase.assertEquals(token.id, loaded.id)

    }
    */

}