package com.example.todonotion.ui.postDetails.di

import com.example.todonotion.ui.postDetails.PostDetailFragment
import dagger.Subcomponent

@Subcomponent(modules = [PostDetailsModule::class])
interface PostDetailsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PostDetailsComponent
    }

    fun inject(fragment: PostDetailFragment)
}