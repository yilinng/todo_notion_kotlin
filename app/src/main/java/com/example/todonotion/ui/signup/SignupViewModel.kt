package com.example.todonotion.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.RemoteAuthRepository
import com.example.todonotion.data.local.token.Token
import com.example.todonotion.data.model.Signup
import com.example.todonotion.data.model.SignupResponse
import com.example.todonotion.data.model.User
import com.example.todonotion.overview.auth.UserApiStatus
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class SignupViewModel @Inject constructor(private val remoteAuthRepository: RemoteAuthRepository) :
    ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<UserApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<UserApiStatus> = _status

    private val _signupResponse = MutableLiveData<SignupResponse>()
    private val signupResponse: LiveData<SignupResponse> = _signupResponse

    private val _token = MutableLiveData<Token?>()
    val token: LiveData<Token?> = _token

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun signupAction(signup: Signup) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _signupResponse.value = remoteAuthRepository.signupUser(signup)
                _token.value = Token(
                    accessToken = signupResponse.value!!.accessToken,
                    refreshToken = signupResponse.value!!.newToken.refreshToken,
                    userId = signupResponse.value!!.newUser.id
                )
                _user.value = User(
                    name = signupResponse.value!!.newUser.name,
                    email = signupResponse.value!!.newUser.email,
                    id = signupResponse.value!!.newUser.id,
                    todos = signupResponse.value!!.newUser.todos
                )
                _status.value = UserApiStatus.DONE
                // Log.i("signupUser200",  signupResponse.value.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
                // Log.i("signupUser400", error.value.toString())

            }
        }
    }

    private fun initError() {
        _error.value = null
    }

    fun initToken() {
        _token.value = null
    }

    fun isSignupEntryValid(name: String, email: String, password: String): Boolean {
        return !(name.isBlank() || email.isBlank() || password.isBlank())
    }
}