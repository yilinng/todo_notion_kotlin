package com.example.todonotion.ui.postList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.RemoteAuthRepository
import com.example.todonotion.data.model.Post
import com.example.todonotion.overview.auth.UserApiStatus
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject


class PostListViewModel @Inject constructor(private val remoteAuthRepository: RemoteAuthRepository) :
    ViewModel() {
    private val _status = MutableLiveData<UserApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<UserApiStatus> = _status

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    private val _filteredPosts = MutableLiveData<List<Post>>()
    private val filteredPosts: LiveData<List<Post>> = _filteredPosts

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error


    init {
        getPostsAction()
    }

    private fun getPostsAction() {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _posts.value = remoteAuthRepository.getTodos()
                _status.value = UserApiStatus.DONE

                _posts.value = posts.value!!.sortedByDescending {
                    it.updateDate
                }
                // Log.i("getPosts200", posts.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                // Log.e("getPosts404", error.value.toString())

            }
        }
    }

    fun onPostClicked(post: Post) {
        // TODO: Set the post object
        _post.value = post
    }

    fun selectedTab(tab: TabLayout.Tab?) {
        if (tab != null) {
            Log.d("selectedTab", tab.text.toString())
            if (tab.text == "All post") {
                //use first get todos
                getPostsAction()
            } else {
                if (posts.value!!.isNotEmpty()) {
                    //store last filtered todos
                    _posts.value = filteredPosts.value
                }
            }
        }
    }

    fun filteredPost(userId: String) {
        if (posts.value!!.isNotEmpty()) {
            _filteredPosts.value = posts.value!!.filter {
                userId == it.user.id
            }
        } else {
            _filteredPosts.value = posts.value
        }
    }

    private fun initError() {
        _error.value = null
    }


    fun cleanPost() {
        _post.value = null
    }
}