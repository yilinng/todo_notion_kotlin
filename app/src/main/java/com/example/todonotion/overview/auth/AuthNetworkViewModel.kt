package com.example.todonotion.overview.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.R
import com.example.todonotion.data.Token.Token
import com.example.todonotion.network.Login
import com.example.todonotion.network.Post
import com.example.todonotion.network.Signup
import com.example.todonotion.network.User
import com.example.todonotion.network.UserApi
import com.example.todonotion.network.dto.PostDto
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException

enum class UserApiStatus { LOADING, ERROR, DONE }

class AuthNetworkViewModel: ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<UserApiStatus>()
    // The external immutable LiveData for the request status
    val status: LiveData<UserApiStatus> = _status

    private val _token = MutableLiveData<Token>()
    val token: LiveData<Token> = _token

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _filteredPosts = MutableLiveData<List<Post>>()
    private val filteredPosts: LiveData<List<Post>> = _filteredPosts

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    private val _response = MutableLiveData<ResponseBody>()
    val response: LiveData<ResponseBody> = _response

    private  val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        getPostsAction()
    }


    //https://stackoverflow.com/questions/33815515/how-do-i-get-response-body-when-there-is-an-error-when-using-retrofit-2-0-observ
    fun loginAction(login: Login) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
             //   UserApi.retrofitService.loginUser(login)
                _token.value = UserApi.retrofitService.loginUser(login)
                _status.value = UserApiStatus.DONE
                Log.i("loginUser200",  UserApi.retrofitService.loginUser(login).toString())
            }catch(e: HttpException) {
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
                _response.value = UserApi.retrofitService.signupUser(signup)
                _status.value = UserApiStatus.DONE
                Log.i("signupUser200",  response.toString())
            }catch(e: HttpException) {
                _status.value = UserApiStatus.ERROR
                _error.value = e.response()?.errorBody()?.string()
                Log.i("signupUser400",  error.value.toString())

            }
        }
    }

    fun getUserAction(token: String ) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            Log.d("getUser accessToken", "Bearer $accessToken")
            //Log.d("getUser accessToken", "Bearer $token")
            try {
                _user.value = UserApi.retrofitService.getUser(authorization= "Bearer $token")
                _status.value = UserApiStatus.DONE
                Log.d("getUser200",  user.toString())

                _filteredPosts.value = user.value!!.todos
            } catch (e: Exception) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                Log.e("getUser404", e.toString())

            }
        }
    }

    private fun getPostsAction() {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _posts.value = UserApi.retrofitService.getTodos()
                _status.value = UserApiStatus.DONE
                Log.i("getTodos200",  posts.toString())

            } catch (e: Exception) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                Log.e("getTodos404", e.toString())

            }
        }
    }

    fun getPostAction(postId: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _post.value = UserApi.retrofitService.getTodo(postId)
                _status.value = UserApiStatus.DONE
                Log.i("getTodo200",  post.toString())

            } catch (e: Exception) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                Log.e("getTodo404", e.toString())

            }
        }
    }

    fun getSearchAction(title: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            try {
                _posts.value = UserApi.retrofitService.searchTodo(title)
                _status.value = UserApiStatus.DONE
                Log.i("getSearchTodo200",  posts.toString())

            } catch (e: Exception) {
                _status.value = UserApiStatus.ERROR
                _posts.value = listOf()
                Log.e("getSearchTodo404", e.toString())

            }
        }
    }

    fun addPostAction(postDto: PostDto) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            Log.d("addPostAction accessToken", "Bearer $accessToken")
            try {
                _post.value = UserApi.retrofitService.addTodo(authorization= "Bearer $accessToken", postDto)
                _status.value = UserApiStatus.DONE

                Log.i("addPostTodo200",  post.toString())
                getPostsAction()
            } catch (e: Exception) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                Log.e("addPostTodo404", e.toString())

            }
        }
    }

    fun editPostAction(postId: String, postDto: PostDto) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            Log.d("editPostAction accessToken", "Bearer $accessToken")
            try {
                _post.value = UserApi.retrofitService.editTodo(postId = postId,authorization= "Bearer $accessToken", postDto)
                _status.value = UserApiStatus.DONE
                Log.i("editPost200",  post.toString())
                getPostsAction()
            } catch (e: Exception) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                Log.e("editPostTodo404", e.toString())

            }
        }
    }

    fun deletePostAction(postId: String) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            Log.d("deletePostAction accessToken", "Bearer $accessToken")
            try {
                _response.value = UserApi.retrofitService.deleteTodo(postId = postId,authorization= "Bearer $accessToken")
                _status.value = UserApiStatus.DONE
                Log.i("deletePost200",  response.toString())
                getPostsAction()
            } catch (e: Exception) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                Log.e("deletePost404", e.toString())

            }
        }
    }

    fun initUser(){
        _user.value = null
    }

    fun setToken(token: Token) {
        _token.value = token
    }

    private fun getToken(): String{
        return if(token.value != null) {
            token.value!!.accessToken
        }else {
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

    fun subSequenceTitle(title: String):String{
        if(title.length > 50) {
           return title.subSequence(0, 50).toString() + "..."
        }
        return title
    }

    //https://stackoverflow.com/questions/48096204/in-kotlin-how-to-check-contains-one-or-another-value
    fun checkUserHavePost(post: Post):Boolean{
       return user.value!!.todos.any{
           it.id == post.id
       }
    }

    fun cleanPost() {
        _post.value = null
    }

    fun selectedTab(tab: TabLayout.Tab?){
        if (tab != null) {
            Log.d("selectedTab", tab.text.toString())
            if(tab.text == "All post") {
                //use first get todos
                getPostsAction()
            }else {
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
    fun isSignupEntryValid(name: String, username: String, email: String, password: String): Boolean {
        return !(name.isBlank() || username.isBlank() || email.isBlank() || password.isBlank())
    }

    fun isLoginEntryValid(email: String, password: String): Boolean {
        return !(email.isBlank() || password.isBlank())
    }

    fun isPostEntryValid(title: String, content: String): Boolean {
        return !(title.isBlank() || content.isBlank())
    }
}