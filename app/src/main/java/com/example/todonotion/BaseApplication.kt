package com.example.todonotion

import android.app.Application
import com.example.todonotion.data.KeyDatabase

/**
 * An application class that inherits from [Application], allows for the creation of a singleton
 * instance of the [KeyDatabase]
 */
class BaseApplication : Application() {

    // TODO: provide a ForageDatabase value by lazy here
    val database: KeyDatabase by lazy { KeyDatabase.getDatabase(this) }
}