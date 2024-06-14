package com.example.todonotion.ui.main.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.TokenViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthNetworkViewModel::class)
    abstract fun bindViewModel(viewmodel: AuthNetworkViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TokenViewModel::class)
    abstract fun bindTokenViewModel(viewmodel: TokenViewModel): ViewModel


}