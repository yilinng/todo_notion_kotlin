package com.example.todonotion.ui.todoList.di

import com.example.todonotion.ui.todoList.TodoListFragment
import dagger.Subcomponent

@Subcomponent(modules = [TodoListModule::class])
interface TodoListComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoListComponent
    }

    fun inject(fragment: TodoListFragment)
}