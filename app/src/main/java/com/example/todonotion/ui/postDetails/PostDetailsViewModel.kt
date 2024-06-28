package com.example.todonotion.ui.postDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.RemoteAuthRepository
import com.example.todonotion.data.model.UpdateNestedPost

import com.example.todonotion.overview.auth.UserApiStatus
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject


class PostDetailsViewModel @Inject constructor(private val remoteAuthRepository: RemoteAuthRepository) : ViewModel() {

    private val _status = MutableLiveData<UserApiStatus>()
    val status: LiveData<UserApiStatus> = _status

    private val _post = MutableLiveData<UpdateNestedPost?>()
    val post: LiveData<UpdateNestedPost?> = _post

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getPostAction(postId: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _post.value = remoteAuthRepository.getTodo(postId).todo
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