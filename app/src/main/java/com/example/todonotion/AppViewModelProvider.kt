package com.example.todonotion

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
//import com.example.todonotion.ui.todoSearch.KeyViewModel
import com.example.todonotion.overview.auth.TokenViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for SearchViewModel
        /*
        initializer {
           KeyViewModel(baseApplication().container.keywordsRepository)
        }

        // Initializer for TokenViewModel
        initializer {
            TokenViewModel(baseApplication().container.tokensRepository)
        }
         */
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [BaseApplication].
 */
fun CreationExtras.baseApplication(): BaseApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BaseApplication)