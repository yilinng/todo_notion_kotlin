package com.example.todonotion.ui.todoSearchResult.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.ui.todoSearchResult.KeywordViewModel

import com.example.todonotion.ui.todoSearchResult.TodoSearchResultViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class TodoSearchResultModule {
    @Binds
    @IntoMap
    @ViewModelKey(TodoSearchResultViewModel::class)
    abstract fun bindViewModel(todoSearchResultViewModel: TodoSearchResultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeywordViewModel::class)
    abstract fun bindKeywordViewModel(keywordViewModel: KeywordViewModel): ViewModel
}