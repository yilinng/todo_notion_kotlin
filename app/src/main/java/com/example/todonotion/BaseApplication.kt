package com.example.todonotion

import android.app.Application
import com.example.todonotion.data.AppContainer
import com.example.todonotion.data.DefaultAppContainer
import com.example.todonotion.data.KeyDatabase
import com.example.todonotion.di.AppComponent
import com.example.todonotion.di.DaggerAppComponent

/**
 * https://stackoverflow.com/questions/74180208/kotlin-1-7-10-with-dagger-a-failure-occurred-while-executing-org-jetbrains-kotl
 * https://stackoverflow.com/questions/42843996/android-kotlin-error-unresolved-reference-daggerappcomponent
 * An application class that inherits from [Application], allows for the creation of a singleton
 * instance of the [KeyDatabase]
 */
open class BaseApplication : Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    //lateinit var container: AppContainer

    // Instance of the AppComponent that will be used by all the Activities in the project
    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): AppComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        return DaggerAppComponent.factory().create(applicationContext)
    }

    // TODO: provide a ForageDatabase value by lazy here
   // val database: KeyDatabase by lazy { KeyDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        //container = DefaultAppContainer(this)
    }
}