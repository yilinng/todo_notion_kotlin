package com.example.todonotion.di

import android.content.Context
import androidx.room.Room
import com.example.todonotion.data.KeyDatabase
import com.example.todonotion.data.Keyword.KeywordsRepository
import com.example.todonotion.data.Keyword.OfflineKeywordsRepository
import com.example.todonotion.data.NetworkTodosRepository
import com.example.todonotion.data.NetworkUsersRepository
import com.example.todonotion.data.TodosRepository
import com.example.todonotion.data.Token.TokensRepository
import com.example.todonotion.data.Token.OfflineTokensRepository
import com.example.todonotion.data.UsersRepository
import com.example.todonotion.network.TodoApiService
import com.example.todonotion.network.UserApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier

import javax.inject.Singleton

@Module
object AppModule {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class NetworkTodosRepository

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class NetworkUsersRepository


    @JvmStatic
    @Singleton
    @Provides
    fun provideTodoApiService(): TodoApiService {
        return Retrofit.Builder()
            .baseUrl("https://pixabay.com/api/?key=40521554-653259fd6834861c55e904c4e")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TodoApiService::class.java)
    }

    @JvmStatic
    @Provides
    @NetworkTodosRepository
    @Singleton
    fun provideTodoRepository(todoApiService: TodoApiService): TodosRepository {
        return NetworkTodosRepository(todoApiService)
    }


    @JvmStatic
    @Singleton
    @Provides
    fun provideUserApiService(): UserApiService {
        return Retrofit.Builder()
            .baseUrl("https://express-api-react-notion.vercel.app/api/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UserApiService::class.java)
    }


    @JvmStatic
    @Provides
    @NetworkUsersRepository
    @Singleton
    fun provideUserRepository(userApiService: UserApiService): UsersRepository {
        return NetworkUsersRepository(userApiService)
    }


    @JvmStatic
    @Singleton
    @Provides
    fun provideLocalToken(
        database: KeyDatabase
    ): TokensRepository {
        return OfflineTokensRepository(
            database.tokenDao()
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideLocalKeyword(
        database: KeyDatabase
    ): KeywordsRepository {
        return OfflineKeywordsRepository(
            database.keyDao()
        )
    }


    @Volatile
    private var INSTANCE: KeyDatabase? = null

    @JvmStatic
    @Singleton
    @Provides
    fun provideDataBase(context: Context): KeyDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                KeyDatabase::class.java,
                "notion_database"
            )
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            // return instance
            instance
        }
    }

}