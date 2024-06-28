package com.example.todonotion.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.RemoteAuthRepository
import com.example.todonotion.data.local.token.Token

import com.example.todonotion.data.model.AuthResponse
import com.example.todonotion.data.model.Login
import com.example.todonotion.data.model.User
import com.example.todonotion.overview.auth.UserApiStatus
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject


class LoginViewModel @Inject constructor(
    private val remoteAuthRepository: RemoteAuthRepository
) : ViewModel() {

    private val _status = MutableLiveData<UserApiStatus>()
    val status: LiveData<UserApiStatus> = _status

    private val _authResponse = MutableLiveData<AuthResponse>()
    val authResponse: LiveData<AuthResponse> = _authResponse

    private val _token = MutableLiveData<Token?>()
    val token: LiveData<Token?> = _token

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loginAction(login: Login) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                //   UserApi.retrofitService.loginUser(login)
                _authResponse.value = remoteAuthRepository.loginUser(login)
                //store token
                _token.value = Token(
                    accessToken = authResponse.value!!.accessToken,
                    refreshToken = authResponse.value!!.newToken.refreshToken,
                    userId = authResponse.value!!.user.id
                )

                _user.value = User(
                    name = authResponse.value!!.user.name,
                    email = authResponse.value!!.user.email,
                    id = authResponse.value!!.user.id,
                    todos = authResponse.value!!.user.todos
                )
                _status.value = UserApiStatus.DONE
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
            }
        }
    }

    fun isLoginEntryValid(email: String, password: String): Boolean {
        return !(email.isBlank() || password.isBlank())
    }

    private fun initError() {
        _error.value = null
    }

   fun initToken() {
        _token.value = null
    }
}