package com.example.todonotion.ui.todoSearch.di

import com.example.todonotion.ui.todoSearch.TodoSearchFragment


import dagger.Subcomponent

@Subcomponent(modules = [TodoSearchModule::class])
interface TodoSearchComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoSearchComponent
    }

    fun inject(fragment: TodoSearchFragment)
}