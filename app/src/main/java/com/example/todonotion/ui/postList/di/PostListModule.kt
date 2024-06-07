package com.example.todonotion.ui.postList.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.postDetails.PostDetailsViewModel
import com.example.todonotion.ui.postList.PostListViewModel
import com.example.todonotion.ui.signup.SignupViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PostListModule {
    @Binds
    @IntoMap
    @ViewModelKey(PostListViewModel::class)
    abstract fun bindViewModel(viewmodel: PostListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TokenViewModel::class)
    abstract fun bindTokenViewModel(viewmodel: TokenViewModel): ViewModel
}