package com.example.todonotion.ui.login.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.login.LoginViewModel
import com.example.todonotion.ui.signup.SignupViewModel
import com.example.todonotion.ui.todoSearch.TodoSearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LoginModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindViewModel(viewmodel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TokenViewModel::class)
    abstract fun bindTokenViewModel(viewmodel: TokenViewModel): ViewModel
}