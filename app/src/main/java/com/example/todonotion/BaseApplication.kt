package com.example.todonotion

import android.app.Application
import com.example.todonotion.data.AppContainer
import com.example.todonotion.data.DefaultAppContainer
import com.example.todonotion.data.KeyDatabase
import com.example.todonotion.data.Keyword.KeywordsRepository
import com.example.todonotion.data.TodosRepository
import com.example.todonotion.data.Token.TokensRepository

/**
 * An application class that inherits from [Application], allows for the creation of a singleton
 * instance of the [KeyDatabase]
 */
class BaseApplication : Application() {


    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer

    // TODO: provide a ForageDatabase value by lazy here
    val database: KeyDatabase by lazy { KeyDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}