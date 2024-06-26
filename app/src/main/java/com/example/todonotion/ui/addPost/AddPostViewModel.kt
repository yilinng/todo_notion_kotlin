package com.example.todonotion.ui.addPost

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.RemoteAuthRepository
import com.example.todonotion.data.local.token.Token

import com.example.todonotion.data.model.AuthResponse
import com.example.todonotion.data.model.NestedPost
import com.example.todonotion.data.model.Post
import com.example.todonotion.data.model.SignupResponse
import com.example.todonotion.data.model.UpdateNestedPost
import com.example.todonotion.data.model.UpdatePost
import com.example.todonotion.data.model.UpdateToken
import com.example.todonotion.data.model.User
import com.example.todonotion.data.model.dto.PostDto
import com.example.todonotion.overview.auth.UserApiStatus
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class AddPostViewModel @Inject constructor(private val remoteAuthRepository: RemoteAuthRepository) :
    ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<UserApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<UserApiStatus> = _status

    private val _authResponse = MutableLiveData<AuthResponse>()
    val authResponse: LiveData<AuthResponse> = _authResponse

    private val _signupResponse = MutableLiveData<SignupResponse>()
    val signupResponse: LiveData<SignupResponse> = _signupResponse

    private val _addPost = MutableLiveData<NestedPost>()
    val addPost: LiveData<NestedPost> = _addPost

    private val _editPost = MutableLiveData<UpdatePost>()
    val editPost: LiveData<UpdatePost> = _editPost

    private val _token = MutableLiveData<Token?>()
    val token: LiveData<Token?> = _token

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _filteredPosts = MutableLiveData<List<Post>>()
    val filteredPosts: LiveData<List<Post>> = _filteredPosts

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    //private val _response = MutableLiveData<ResponseBody?>()
    //val response: LiveData<ResponseBody?> = _response

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

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


    fun getPostAction(postId: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _post.value = covertUpdateNestedPostToPost(remoteAuthRepository.getTodo(postId).todo)
                _status.value = UserApiStatus.DONE
                Log.i("getTodo200_addPost", post.value.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                Log.e("getTodos404_addPost", error.value.toString())
            }
        }
    }



    fun addPostAction(postDto: PostDto) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            // Log.d("addPostAction", "Bearer $accessToken")
            try {
                _addPost.value =
                    remoteAuthRepository.addTodo(authorization = "Bearer $accessToken", postDto)

                _user.value = addPost.value!!.user

                _post.value = user.value?.let {
                    Post(
                        id = editPost.value!!.todo.id,
                        title = addPost.value!!.todo.title,
                        context = addPost.value!!.todo.context,
                        updateDate = addPost.value!!.todo.updateDate,
                        user = it,
                        username = addPost.value!!.todo.username
                    )
                }

                _status.value = UserApiStatus.DONE

                //   Log.i("addPostTodo200", fromResponse.toString())
                getPostsAction()
                // filteredPost()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                //   Log.e("addPostTodo404", error.value.toString())

            }
        }
    }

    fun editPostAction(postId: String, postDto: PostDto) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            // Log.d("editPostAction accessToken", "Bearer $accessToken")
            try {
                _editPost.value = remoteAuthRepository.editTodo(
                    postId = postId,
                    authorization = "Bearer $accessToken",
                    postDto
                )
                _post.value = user.value?.let {
                    Post(
                        id = editPost.value!!.todo.id,
                        title = editPost.value!!.todo.title,
                        context = editPost.value!!.todo.context,
                        updateDate = editPost.value!!.todo.updateDate,
                        user = it,
                        username = editPost.value!!.todo.username
                    )
                }
                _status.value = UserApiStatus.DONE
                //  Log.i("editPost200", fromResponse.toString())
                getPostsAction()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                //  Log.e("editPostTodo404", error.value.toString())

            }
        }
    }


    fun deletePostAction(postId: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            //Log.d("deletePostAction accessToken", "Bearer $accessToken")
            try {
                remoteAuthRepository.deleteTodo(
                    postId = postId,
                    authorization = "Bearer $accessToken"
                )
                _status.value = UserApiStatus.DONE
                //  Log.i("deletePost204", "deletePost204")
                getPostsAction()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()

                //   Log.e("deletePost404", error.value.toString())

            }
        }
    }

    fun updateToken() {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            //val accessToken = getToken()
            //Log.d("updateToken", token.value.toString())
            //Log.d("getUser accessToken", "Bearer $token")
            try {
                val fromToken =
                    remoteAuthRepository.updateToken(UpdateToken(token = token.value!!.refreshToken))
                _token.value = Token(
                    id = token.value!!.id,
                    accessToken = fromToken.accessToken,
                    refreshToken = token.value!!.refreshToken,
                    userId = token.value!!.userId
                )

                _status.value = UserApiStatus.DONE

                // Log.d("getToken200", token.value.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
                //_posts.value = listOf()
                // Log.e("getToken404", error.value.toString())

            }
        }
    }


    //https://stackoverflow.com/questions/48096204/in-kotlin-how-to-check-contains-one-or-another-value
    fun checkUserHavePost(post: Post): Boolean {
        return user.value?.id == post.user.id
    }


    private fun initError() {
        _error.value = null
    }

    private fun getToken(): String {
        return if (token.value != null) {
            token.value!!.accessToken
        } else {
            ""
        }
    }

    fun setToken(token: Token) {
        _token.value = token
    }

    fun setUser(user: User) {
        _user.value = user
    }

    fun initUser() {
        _user.value = null
    }

    fun isPostEntryValid(title: String, content: String, username: String): Boolean {
        return !(title.isBlank() || content.isBlank() || username.isBlank())
    }


    private fun covertUpdateNestedPostToPost(updateNestedPost: UpdateNestedPost): Post? {
        return user.value?.let {
            Post(
                id = updateNestedPost.id,
                title = updateNestedPost.title,
                context = updateNestedPost.context,
                updateDate = updateNestedPost.updateDate,
                username = updateNestedPost.username,
                user = it
            )
        }
    }

}