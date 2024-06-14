package com.example.todonotion.ui.main.di

import com.example.todonotion.ui.main.LoggedUserScope
import com.example.todonotion.ui.main.MainActivity
import com.example.todonotion.ui.postList.di.PostListModule
import dagger.Subcomponent

// Scope annotation that the MainComponent uses
// Classes annotated with @LoggedUserScope will have a unique instance in this Component
@LoggedUserScope
@Subcomponent(modules = [MainModule::class])
interface MainComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(activity: MainActivity)
}