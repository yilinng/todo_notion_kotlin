package com.example.todonotion.ui.todoList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.TodosRepository

import com.example.todonotion.model.Todo
import com.example.todonotion.overview.TodoApiStatus

import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject


class TodoListViewModel @Inject constructor(private val todosRepository: TodosRepository) :
    ViewModel() {
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<TodoApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<TodoApiStatus> = _status

    private val _todos = MutableLiveData<List<Todo>>()
    val todos: LiveData<List<Todo>> = _todos

    private val _todo = MutableLiveData<Todo?>()
    val todo: LiveData<Todo?> = _todo

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _filteredTodos = MutableLiveData<List<Todo>>()
    val filteredTodos: LiveData<List<Todo>> = _filteredTodos

    private val _foundData = MutableLiveData<Boolean>()
    private val foundData: LiveData<Boolean> = _foundData

    init {
        getPostsAction()
    }

    private fun getPostsAction() {
        viewModelScope.launch {
            _status.value = TodoApiStatus.LOADING
            try {
                _todos.value = todosRepository.getPhotos().hits
                _status.value = TodoApiStatus.DONE

                // Log.i("getPosts200", posts.toString())
                initError()
            } catch (e: HttpException) {
                _status.value = TodoApiStatus.ERROR
                _todos.value = listOf()
                _error.value = e.response()?.errorBody()?.string()
                // Log.e("getPosts404", error.value.toString())

            }
        }
    }

    fun selectedTab(tab: TabLayout.Tab?) {
        if (tab != null) {
            //Log.d("selectedTab", tab.text.toString())
            if (tab.text == "Recent") {
                //use first get todos
                getPostsAction()
            } else {
                //store last filtered todos
                _todos.value = filteredTodos.value
            }
        }
    }

    fun dataNotFound(): String {
        return if (foundData.value == false || todos.value?.isEmpty() == true) {
            "data not found."
        } else {
            ""
        }
    }

    fun onTodoClicked(todo: Todo) {
        // TODO: Set the todo object
        _todo.value = todo
    }

    private fun initError() {
        _error.value = null
    }

   fun initTodo() {
        _todo.value = null
    }
}