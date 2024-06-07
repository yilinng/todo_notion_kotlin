package com.example.todonotion.ui.postDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.UsersRepository
import com.example.todonotion.model.Post
import com.example.todonotion.model.UpdateNestedPost
import com.example.todonotion.model.UpdatePost
import com.example.todonotion.model.User
import com.example.todonotion.overview.auth.UserApiStatus
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject


class PostDetailsViewModel @Inject constructor(val usersRepository: UsersRepository) : ViewModel() {

    private val _status = MutableLiveData<UserApiStatus>()
    val status: LiveData<UserApiStatus> = _status

    private val _post = MutableLiveData<UpdateNestedPost?>()
    val post: LiveData<UpdateNestedPost?> = _post

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getPostAction(postId: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _post.value = usersRepository.getTodo(postId).todo
                _status.value = UserApiStatus.DONE
                // Log.i("getTodo200", post.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                //  Log.e("getTodos404", error.value.toString())
            }
        }
    }

    private fun initError() {
        _error.value = null
    }


}