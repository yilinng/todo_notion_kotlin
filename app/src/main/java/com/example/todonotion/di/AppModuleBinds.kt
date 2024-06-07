package com.example.todonotion.di

import com.example.todonotion.data.NetworkTodosRepository
import com.example.todonotion.data.NetworkUsersRepository
import com.example.todonotion.data.TodosRepository
import com.example.todonotion.data.UsersRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModuleBinds {
    @Singleton
    @Binds
    abstract fun bindTodoRepository(repo: NetworkTodosRepository): TodosRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(repo: NetworkUsersRepository): UsersRepository
}