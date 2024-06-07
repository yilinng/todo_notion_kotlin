package com.example.todonotion.ui.signup.di

import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.signup.SignupFragment
import dagger.Subcomponent

@Subcomponent(modules = [SignupModule::class])
interface SignupComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SignupComponent
    }

    fun inject(fragment: SignupFragment)
}