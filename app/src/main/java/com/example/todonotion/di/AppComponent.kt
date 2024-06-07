package com.example.todonotion.di

import android.content.Context
import com.example.todonotion.MainActivity
import com.example.todonotion.ui.addPost.di.AddPostComponent
import com.example.todonotion.ui.login.di.LoginComponent
import com.example.todonotion.ui.postDetails.di.PostDetailsComponent
import com.example.todonotion.ui.postList.di.PostListComponent
import com.example.todonotion.ui.signup.di.SignupComponent
import com.example.todonotion.ui.todoDetails.di.TodoDetailsComponent
import com.example.todonotion.ui.todoList.di.TodoListComponent
import com.example.todonotion.ui.todoSearch.di.TodoSearchComponent
import com.example.todonotion.ui.todoSearchResult.di.TodoSearchResultComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// Definition of a Dagger component
// Classes annotated with @Singleton will have a unique instance in this Component
//https://github.com/android/architecture-samples/blob/dev-dagger/app/src/main/java/com/example/android/architecture/blueprints/todoapp/tasks/TasksFragment.kt
//https://developer.android.com/training/dependency-injection/dagger-android
//https://github.com/philipplackner/ManualDependencyInjection/blob/initial_with_dagger/app/src/main/java/com/plcoding/manualdependencyinjection/MainActivity.kt
//https://stackoverflow.com/questions/74180208/kotlin-1-7-10-with-dagger-a-failure-occurred-while-executing-org-jetbrains-kotl
// Definition of a Dagger component that adds info from the different modules to the graph
@Singleton
@Component(modules = [AppSubcomponents::class, AppModule::class, ViewModelBuilderModule::class, AppModuleBinds::class])
interface AppComponent {

    // Factory to create instances of the AppComponent
    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun todoListComponent(): TodoListComponent.Factory
    // Classes that can be injected by this Component

    fun todoDetailsComponent(): TodoDetailsComponent.Factory

    fun todoSearchComponent(): TodoSearchComponent.Factory

    fun todoSearchResultComponent(): TodoSearchResultComponent.Factory

    fun signupComponent(): SignupComponent.Factory

    fun loginComponent(): LoginComponent.Factory

    fun postListComponent(): PostListComponent.Factory

    fun postDetailsComponent(): PostDetailsComponent.Factory

    fun addPostComponent(): AddPostComponent.Factory
}