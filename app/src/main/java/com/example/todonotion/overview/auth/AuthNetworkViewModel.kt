package com.example.todonotion.overview.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.R
import com.example.todonotion.data.Token.Token
import com.example.todonotion.network.AccessToken
import com.example.todonotion.network.AuthResponse
import com.example.todonotion.network.Login
import com.example.todonotion.network.Post
import com.example.todonotion.network.Signup
import com.example.todonotion.network.UpdateToken
import com.example.todonotion.network.User
import com.example.todonotion.network.UserApi
import com.example.todonotion.network.dto.PostDto
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException

enum class UserApiStatus { LOADING, ERROR, DONE }

class AuthNetworkViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<UserApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<UserApiStatus> = _status

    private val _authResponse = MutableLiveData<AuthResponse>()
    val authResponse: LiveData<AuthResponse> = _authResponse

    private val _token = MutableLiveData<Token?>()
    val token: LiveData<Token?> = _token

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _filteredPosts = MutableLiveData<List<Post>>()
    private val filteredPosts: LiveData<List<Post>> = _filteredPosts

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    private val _response = MutableLiveData<ResponseBody?>()
    val response: LiveData<ResponseBody?> = _response

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error


    init {
        getPostsAction()
    }


    //https://stackoverflow.com/questions/33815515/how-do-i-get-response-body-when-there-is-an-error-when-using-retrofit-2-0-observ
    fun loginAction(login: Login) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                //   UserApi.retrofitService.loginUser(login)
                _authResponse.value = UserApi.retrofitService.loginUser(login)
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
                //  Log.i("loginUser200",  UserApi.retrofitService.loginUser(login).toString())
                Log.i("loginUser200", authResponse.value.toString())

                /*
                _filteredPosts.value = posts.value!!.filter {
                    user.value!!.todos!!.contains(it.id)
                }
                */
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
                Log.i("loginUser400", error.value.toString())
            }
        }
    }

    fun signupAction(signup: Signup) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _authResponse.value = UserApi.retrofitService.signupUser(signup)
                _token.value = Token(
                    accessToken = authResponse.value!!.accessToken,
                    refreshToken = authResponse.value!!.newToken.refreshToken,
                    userId = authResponse.value!!.user.id
                )
                _status.value = UserApiStatus.DONE
                Log.i("signupUser200", response.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
                Log.i("signupUser400", error.value.toString())

            }
        }
    }

    fun logoutAction() {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            Log.d("logout accessToken", "Bearer $accessToken")
            try {
                UserApi.retrofitService.logoutUser(
                    authorization = "Bearer $accessToken"
                )
                _status.value = UserApiStatus.DONE
                Log.i("logout204", "logout204")
                getPostsAction()
                initUser()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
                Log.e("logout404", error.value.toString())

            }
        }
    }

    fun getUserAction(token: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            // val accessToken = getToken()
            Log.d("getUser", "Bearer $token")
            //Log.d("getUser accessToken", "Bearer $token")
            try {
                _user.value = UserApi.retrofitService.getUser(authorization = "Bearer $token").user
                _status.value = UserApiStatus.DONE
                Log.d("getUser200", user.value.toString())

                filteredPost()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
                //_posts.value = listOf()
                Log.e("getUser404", error.value.toString())

            }
        }
    }

    fun updateToken() {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            //val accessToken = getToken()
            Log.d("updateToken", token.value.toString())
            //Log.d("getUser accessToken", "Bearer $token")
            try {
                val fromToken =
                    UserApi.retrofitService.updateToken(UpdateToken(token = token.value!!.refreshToken))
                _token.value = Token(
                    id = token.value!!.id,
                    accessToken = fromToken.accessToken,
                    refreshToken = token.value!!.refreshToken,
                    userId = token.value!!.userId
                )

                _status.value = UserApiStatus.DONE

                Log.d("getToken200", token.value.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
                //_posts.value = listOf()
                Log.e("getToken404", error.value.toString())

            }
        }
    }

    private fun getPostsAction() {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _posts.value = UserApi.retrofitService.getTodos()
                _status.value = UserApiStatus.DONE
                Log.i("getPosts200", posts.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                Log.e("getPosts404", error.value.toString())

            }
        }
    }

    fun getPostAction(postId: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _post.value = UserApi.retrofitService.getTodo(postId)
                _status.value = UserApiStatus.DONE
                Log.i("getTodo200", post.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                Log.e("getTodos404", error.value.toString())
            }
        }
    }

    fun getSearchAction(title: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _filteredPosts.value = UserApi.retrofitService.searchTodo(title)
                _status.value = UserApiStatus.DONE
                Log.i("getSearchTodo200", posts.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                Log.e("getSearchTodo404", error.value.toString())

            }
        }
    }

    fun addPostAction(postDto: PostDto) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            Log.d("addPostAction", "Bearer $accessToken")
            try {
                val fromResponse =
                    UserApi.retrofitService.addTodo(authorization = "Bearer $accessToken", postDto)

                _user.value = fromResponse.user

                _post.value = user.value?.let {
                    Post(
                        id = fromResponse.todo.id,
                        title = fromResponse.todo.title,
                        context = fromResponse.todo.context,
                        updateDate = fromResponse.todo.updateDate,
                        user = it,
                        username = fromResponse.todo.username
                    )
                }


                _status.value = UserApiStatus.DONE

                Log.i("addPostTodo200", fromResponse.toString())
                getPostsAction()
                filteredPost()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                Log.e("addPostTodo404", error.value.toString())

            }
        }
    }

    fun editPostAction(postId: String, postDto: PostDto) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            Log.d("editPostAction accessToken", "Bearer $accessToken")
            try {
                val fromResponse = UserApi.retrofitService.editTodo(
                    postId = postId,
                    authorization = "Bearer $accessToken",
                    postDto
                )
                _post.value = user.value?.let {
                    Post(
                        id = fromResponse.todo.id,
                        title = fromResponse.todo.title,
                        context = fromResponse.todo.context,
                        updateDate = fromResponse.todo.updateDate,
                        user = it,
                        username = fromResponse.todo.username
                    )
                }
                _status.value = UserApiStatus.DONE
                Log.i("editPost200", fromResponse.toString())
                getPostsAction()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                Log.e("editPostTodo404", error.value.toString())

            }
        }
    }

    fun deletePostAction(postId: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            Log.d("deletePostAction accessToken", "Bearer $accessToken")
            try {
                UserApi.retrofitService.deleteTodo(
                    postId = postId,
                    authorization = "Bearer $accessToken"
                )
                _status.value = UserApiStatus.DONE
                Log.i("deletePost204", "deletePost204")
                getPostsAction()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()

                Log.e("deletePost404", error.value.toString())

            }
        }
    }

    fun filteredPost() {
        if (posts.value!!.isNotEmpty() && user.value != null) {
            _filteredPosts.value = posts.value!!.filter {
                user.value!!.todos!!.contains(it.id)
            }
        }
    }

    private fun initUser() {
        _user.value = null
    }

    private fun initError() {
        _error.value = null
    }

    fun setToken(token: Token?) {
        _token.value = token
    }

    private fun getToken(): String {
        return if (token.value != null) {
            token.value!!.accessToken
        } else {
            ""
        }
    }

    fun dataNotFound(): String {
        return if (posts.value?.isEmpty() == true) {
            "data not found."
        } else {
            ""
        }
    }

    fun subSequenceTitle(title: String): String {
        if (title.length > 50) {
            return title.subSequence(0, 50).toString() + "..."
        }
        return title
    }

    //https://stackoverflow.com/questions/48096204/in-kotlin-how-to-check-contains-one-or-another-value
    fun checkUserHavePost(post: Post): Boolean {
        return user.value!!.id == post.user.id
    }


    fun cleanPost() {
        _post.value = null
    }

    fun selectedTab(tab: TabLayout.Tab?) {
        if (tab != null) {
            Log.d("selectedTab", tab.text.toString())
            if (tab.text == "All post") {
                //use first get todos
                getPostsAction()
            } else {
                //store last filtered todos
                _posts.value = filteredPosts.value
            }
        }
    }

    //https://stackoverflow.com/questions/46662513/convert-array-to-list-in-kotlin
    fun onPostClicked(post: Post) {
        // TODO: Set the post object
        _post.value = post
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isSignupEntryValid(name: String, email: String, password: String): Boolean {
        return !(name.isBlank() || email.isBlank() || password.isBlank())
    }

    fun isLoginEntryValid(email: String, password: String): Boolean {
        return !(email.isBlank() || password.isBlank())
    }

    fun isPostEntryValid(title: String, content: String, username: String): Boolean {
        return !(title.isBlank() || content.isBlank() || username.isBlank())
    }
}