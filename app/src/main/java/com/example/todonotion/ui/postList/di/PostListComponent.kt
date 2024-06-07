package com.example.todonotion.ui.postList.di

import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.postList.PostListFragment
import dagger.Subcomponent

@Subcomponent(modules = [PostListModule::class])
interface PostListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PostListComponent
    }

    fun inject(fragment: PostListFragment)
}