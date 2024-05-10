package com.example.todonotion.overview.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch
import com.example.todonotion.data.Token.Token
import com.example.todonotion.data.Token.TokenDao

//store user in database
class TokenViewModel(private val tokenDao: TokenDao): ViewModel() {

    val tokens: LiveData<List<Token>> = tokenDao.getTokens().asLiveData()

    /**
     * Inserts the new Token into database.
     */
    fun addNewToken(token: Token) {
        val newToken = getNewTokenEntry(accessToken= token.accessToken, refreshToken = token.refreshToken, userId = token.userId)
        insertToken(newToken)
    }

    /**
     * Launching a new coroutine to insert a token in a non-blocking way
     */
    fun updateToken(token: Token) {
        viewModelScope.launch {
            tokenDao.update(token)
        }
    }

    /**
     * Launching a new coroutine to insert a token in a non-blocking way
     */
    private fun insertToken(token: Token) {
        viewModelScope.launch {
            tokenDao.insert(token)
        }
    }


    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteToken(token: Token) {
        viewModelScope.launch {
            tokenDao.delete(token)
        }
    }


    /**
     * Returns an instance of the [Token] entity class with the item info entered by the token.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewTokenEntry(accessToken: String, refreshToken: String, userId:String): Token {
        return Token(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
        )
    }
}


/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class TokenViewModelFactory(private val tokenDao: TokenDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TokenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TokenViewModel(tokenDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}