package com.example.todonotion.ui.login.di

import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.login.LoginFragment
import dagger.Subcomponent

@Subcomponent(modules = [LoginModule::class])
interface LoginComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(fragment: LoginFragment)
}