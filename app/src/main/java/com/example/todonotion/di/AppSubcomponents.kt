package com.example.todonotion.di

import com.example.todonotion.ui.addPost.di.AddPostComponent
import com.example.todonotion.ui.login.di.LoginComponent
import com.example.todonotion.ui.postDetails.di.PostDetailsComponent
import com.example.todonotion.ui.postList.di.PostListComponent
import com.example.todonotion.ui.signup.di.SignupComponent
import com.example.todonotion.ui.todoDetails.di.TodoDetailsComponent
import com.example.todonotion.ui.todoList.di.TodoListComponent
import com.example.todonotion.ui.todoSearch.di.TodoSearchComponent
import com.example.todonotion.ui.todoSearchResult.di.TodoSearchResultComponent
import dagger.Module

// This module tells a Component which are its subcomponents
@Module(
    subcomponents = [
        TodoListComponent::class,
        TodoDetailsComponent::class,
        TodoSearchComponent::class,
        TodoSearchResultComponent::class,
        SignupComponent::class,
        LoginComponent::class,
        PostListComponent::class,
        PostDetailsComponent::class,
        AddPostComponent::class
    ]
)

class AppSubcomponents