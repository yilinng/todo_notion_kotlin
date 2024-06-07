package com.example.todonotion.ui.postDetails.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.ui.postDetails.PostDetailsViewModel
import com.example.todonotion.ui.signup.SignupViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PostDetailsModule {
    @Binds
    @IntoMap
    @ViewModelKey(PostDetailsViewModel::class)
    abstract fun bindViewModel(viewmodel: PostDetailsViewModel): ViewModel
}