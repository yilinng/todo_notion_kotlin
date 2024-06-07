package com.example.todonotion.ui.todoSearchResult.di

import com.example.todonotion.ui.todoSearch.KeywordViewModel
import com.example.todonotion.ui.todoSearchResult.TodoSearchResultFragment
import com.example.todonotion.ui.todoSearchResult.TodoSearchResultViewModel
import dagger.Subcomponent

@Subcomponent(modules = [TodoSearchResultModule::class])
    interface TodoSearchResultComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoSearchResultComponent
    }

    fun inject(fragment: TodoSearchResultFragment)
}