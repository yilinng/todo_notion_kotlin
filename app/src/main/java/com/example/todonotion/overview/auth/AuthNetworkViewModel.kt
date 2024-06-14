package com.example.todonotion.overview.auth

import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.RemoteAuthRepository

import com.example.todonotion.data.token.Token
import com.example.todonotion.model.AuthResponse
import com.example.todonotion.model.Login
import com.example.todonotion.model.NestedPost
import com.example.todonotion.model.Post
import com.example.todonotion.model.Signup
import com.example.todonotion.model.SignupResponse
import com.example.todonotion.model.UpdatePost
import com.example.todonotion.model.UpdateToken
import com.example.todonotion.model.User

import com.example.todonotion.model.dto.PostDto

import com.google.android.material.tabs.TabLayout

import kotlinx.coroutines.launch
import javax.inject.Inject
import retrofit2.HttpException

/**
 * AuthNetworkViewModel is the ViewModel that the Registration flow ([MainActivity]
 * and fragments) uses to keep user's input data.
 *
 * @Inject tells Dagger how to provide instances of this type. Dagger also knows
 * that UsersRepository is a dependency.
 */
enum class UserApiStatus { LOADING, ERROR, DONE }

class AuthNetworkViewModel @Inject constructor(private val remoteAuthRepository: RemoteAuthRepository) : ViewModel() {

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


    init {
        getPostsAction()
    }

    //https://stackoverflow.com/questions/33815515/how-do-i-get-response-body-when-there-is-an-error-when-using-retrofit-2-0-observ
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
                //  Log.i("loginUser200",  UserApi.retrofitService.loginUser(login).toString())
               // Log.i("loginUser200", authResponse.value.toString())

                /*
                _filteredPosts.value = posts.value!!.filter {
                    user.value!!.todos!!.contains(it.id)
                }
                */
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
               // Log.i("loginUser400", error.value.toString())
            }
        }
    }

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

    fun logoutAction() {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            //Log.d("logout accessToken", "Bearer $accessToken")
            try {
                remoteAuthRepository.logoutUser(
                    authorization = "Bearer $accessToken"
                )
                _status.value = UserApiStatus.DONE
              //  Log.i("logout204", "logout204")

                initUser()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
              //  Log.e("logout404", error.value.toString())

            }
        }
    }

    fun getUserAction(token: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            // val accessToken = getToken()
            //Log.d("getUser", "Bearer $token")
            //Log.d("getUser accessToken", "Bearer $token")
            try {
                _user.value = remoteAuthRepository.getUser(authorization = "Bearer $token").user
                _status.value = UserApiStatus.DONE
                //Log.d("getUser200", user.value.toString())

                filteredPost()
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
                //_posts.value = listOf()
               // Log.e("getUser404", error.value.toString())

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



    fun getSearchAction(title: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _filteredPosts.value = remoteAuthRepository.searchTodo(title)
                _status.value = UserApiStatus.DONE
             //   Log.i("getSearchTodo200", posts.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _posts.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
             //   Log.e("getSearchTodo404", error.value.toString())

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
                        id = addPost.value!!.todo.id,
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
                filteredPost()
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


    fun filteredPost() {
        if (posts.value!!.isNotEmpty() && user.value != null) {
            _filteredPosts.value = posts.value!!.filter {
                user.value!!.todos!!.contains(it.id)
            }
        } else {
            _filteredPosts.value = posts.value
        }
    }

    private fun initUser() {
        _user.value = null
    }

    private fun initError() {
        _error.value = null
    }

    //https://stackoverflow.com/questions/53304347/mutablelivedata-cannot-invoke-setvalue-on-a-background-thread-from-coroutine
    fun setToken(token: Token?) {
        _token.postValue(token)
    }

    private fun getToken(): String {
        return if (token.value != null) {
            token.value!!.accessToken
        } else {
            ""
        }
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
                if (posts.value!!.isNotEmpty()) {
                    //store last filtered todos
                    _posts.value = filteredPosts.value
                }
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



    /*
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BaseApplication)
                val usersRepository = application.container.usersRepository
                AuthNetworkViewModel(usersRepository = usersRepository)
            }
        }
        private const val TIMEOUT_MILLIS = 5_000L
    }
    */

}