package com.example.todonotion.data

import android.content.Context
import com.example.todonotion.data.keyword.KeywordsRepository
import com.example.todonotion.data.keyword.OfflineKeywordsRepository
import com.example.todonotion.data.token.TokensRepository
import com.example.todonotion.data.token.OfflineTokensRepository
import com.example.todonotion.network.TodoApiService
import com.example.todonotion.network.UserApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface AppContainer {
    val todosRepository: TodosRepository
    val usersRepository: RemoteAuthRepository
    val tokensRepository: TokensRepository
    val keywordsRepository: KeywordsRepository
}

class DefaultAppContainer (private val context: Context) : AppContainer {
    private val baseUrl = "https://pixabay.com/api/"

    private val localBaseUrl = "https://express-api-react-notion.vercel.app/api/"

    /**
     * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
     */
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     * https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter/blob/trunk/README.md
     * https://dev.to/vtsen/simple-rest-api-android-app-in-kotlin-various-http-client-library-implementations-11i2?comments_sort=latest
     */

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseUrl)
        .build()

    private val localRetrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(localBaseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: TodoApiService by lazy {
        retrofit.create(TodoApiService::class.java)
    }

    private val localRetrofitService: UserApiService by lazy {
        localRetrofit.create(UserApiService::class.java)
    }

    /**
     * DI implementation for photos repository
     */
    override val todosRepository: TodosRepository by lazy {
        NetworkTodosRepository(retrofitService)
    }

    override val usersRepository: RemoteAuthRepository by lazy {
       NetworkRemoteAuthRepository(localRetrofitService)
    }

    override val tokensRepository: TokensRepository by lazy {
        OfflineTokensRepository(KeyDatabase.getDatabase(context).tokenDao())
    }

    override val keywordsRepository: KeywordsRepository by lazy {
        OfflineKeywordsRepository(KeyDatabase.getDatabase(context).keyDao())
    }

}