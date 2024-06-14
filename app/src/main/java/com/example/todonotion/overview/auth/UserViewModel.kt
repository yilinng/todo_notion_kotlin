package com.example.todonotion.overview.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch

import com.example.todonotion.data.user.User
import com.example.todonotion.data.user.UsersRepository

//store user in database
class UserViewModel(private val usersRepository: UsersRepository): ViewModel() {

    private val users: LiveData<List<User>> = usersRepository.getAllUsersStream().asLiveData()

    /**
     * Inserts the new User into database.
     */
    fun addNewUser(name: String, email:String) {
        val newUser = getNewUserEntry(name, email)
        insertUser(newUser)
    }


    /**
     * Launching a new coroutine to insert a user in a non-blocking way
     */
    private fun insertUser(user: User) {
        viewModelScope.launch {
            usersRepository.insertUser(user)
        }
    }


    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteUser(user: User) {
        viewModelScope.launch {
            usersRepository.deleteUser(user)
        }
    }


    /**
     * Returns an instance of the [User] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewUserEntry(name: String, email:String): User {
        return User(
            name = name,
            email = email,
        )
    }
}


/**
 * Factory class to instantiate the [UserViewModel] instance.

class AuthViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KeyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
 */