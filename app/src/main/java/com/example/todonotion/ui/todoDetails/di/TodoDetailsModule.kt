package com.example.todonotion.ui.todoDetails.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.ui.todoDetails.TodoDetailsViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class TodoDetailsModule {
    @Binds
    @IntoMap
    @ViewModelKey(TodoDetailsViewModel::class)
    abstract fun bindViewModel(viewmodel: TodoDetailsViewModel): ViewModel
}