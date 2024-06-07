package com.example.todonotion.ui.todoDetails.di

import com.example.todonotion.ui.todoDetails.TodoDetailFragment
import dagger.Subcomponent

@Subcomponent(modules = [TodoDetailsModule::class])
interface TodoDetailsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoDetailsComponent
    }

    fun inject(fragment: TodoDetailFragment)
}