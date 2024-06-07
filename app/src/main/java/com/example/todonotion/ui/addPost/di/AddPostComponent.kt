package com.example.todonotion.ui.addPost.di

import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.addPost.AddPostFragment
import dagger.Subcomponent

@Subcomponent(modules = [AddPostModule::class])
interface AddPostComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AddPostComponent
    }

    fun inject(fragment: AddPostFragment)
}