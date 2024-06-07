package com.example.todonotion.ui.signup.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.signup.SignupViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SignupModule {
    @Binds
    @IntoMap
    @ViewModelKey(SignupViewModel::class)
    abstract fun bindViewModel(viewmodel: SignupViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(TokenViewModel::class)
    abstract fun bindTokenViewModel(viewmodel: TokenViewModel): ViewModel


}