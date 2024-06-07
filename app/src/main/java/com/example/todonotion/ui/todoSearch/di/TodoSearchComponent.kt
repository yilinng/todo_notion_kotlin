package com.example.todonotion.ui.todoSearch.di

import com.example.todonotion.ui.todoSearch.TodoSearchFragment
import com.example.todonotion.ui.todoSearch.TodoSearchViewModel
import com.example.todonotion.ui.todoSearch.KeywordViewModel
import dagger.Subcomponent

@Subcomponent(modules = [TodoSearchModule::class])
interface TodoSearchComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoSearchComponent
    }

    fun inject(fragment: TodoSearchFragment)
}