package com.example.todonotion.ui.todoList.di

import androidx.lifecycle.ViewModel
import com.example.todonotion.di.ViewModelKey
import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.todoList.TodoListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
@Module
abstract class TodoListModule {
    @Binds
    @IntoMap
    @ViewModelKey(TodoListViewModel::class)
    abstract fun bindViewModel(viewmodel: TodoListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthNetworkViewModel::class)
    abstract fun bindAuthViewModel(viewmodel: AuthNetworkViewModel): ViewModel
}