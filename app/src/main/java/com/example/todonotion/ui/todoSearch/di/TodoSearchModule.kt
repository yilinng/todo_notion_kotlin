package com.example.todonotion.ui.todoSearch.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.ui.todoSearch.TodoSearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class TodoSearchModule {
    @Binds
    @IntoMap
    @ViewModelKey(TodoSearchViewModel::class)
    abstract fun bindViewModel(viewmodel: TodoSearchViewModel): ViewModel

}