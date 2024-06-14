package com.example.todonotion.ui.main

import com.example.todonotion.ui.main.di.MainComponent
import javax.inject.Inject

//https://github.com/android/codelab-android-dagger/blob/solution/app/src/main/java/com/example/android/dagger/user/UserManager.kt
class UserManager @Inject constructor(private val mainComponentFactory: MainComponent.Factory){

    var mainComponent: MainComponent? = null
        private set

    fun isUserLoggedIn() = mainComponent != null

    init{
        userJustLoggedIn()
    }

    private fun userJustLoggedIn() {
        // When the user logs in, we create a new instance of UserComponent
        mainComponent = mainComponentFactory.create()
    }
}