package com.example.todonotion.ui.addPost.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.addPost.AddPostViewModel
import com.example.todonotion.ui.postDetails.PostDetailsViewModel
import com.example.todonotion.ui.signup.SignupViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AddPostModule {
    @Binds
    @IntoMap
    @ViewModelKey(AddPostViewModel::class)
    abstract fun bindViewModel(viewmodel: AddPostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TokenViewModel::class)
    abstract fun bindTokenViewModel(viewmodel: TokenViewModel): ViewModel
}