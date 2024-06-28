package com.example.todonotion.ui.todoSearchResult

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.TodosRepository
import com.example.todonotion.data.model.Todo
import com.example.todonotion.overview.TodoApiStatus
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoSearchResultViewModel@Inject constructor(private val todosRepository: TodosRepository) : ViewModel() {

    private val _status = MutableLiveData<TodoApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<TodoApiStatus> = _status

    private val _todos = MutableLiveData<List<Todo>>()
    val todos: LiveData<List<Todo>> = _todos

    private val _todo = MutableLiveData<Todo?>()
    val todo: LiveData<Todo?> = _todo

    private val _foundData = MutableLiveData<Boolean>()
    private val foundData: LiveData<Boolean> = _foundData

    private val _keyword = MutableLiveData<String?>()
    val keyword: LiveData<String?> = _keyword


    fun getTodoPhotosByKeyWord(string: String) {
        viewModelScope.launch {
            _status.value = TodoApiStatus.LOADING
            try {
                _todos.value = todosRepository.getPhotosWithKey(string).hits
                _status.value = TodoApiStatus.DONE
                 Log.i("todoSearchResult_getTodoPhotosByKeyWord200", todos.value.toString())

            } catch (e: Exception) {
                _status.value = TodoApiStatus.ERROR
                _todos.value = listOf()
                 Log.e("todoSearchResult_getTodoPhotosByKeyWord404", e.toString())
            } finally {
                if (todos.value!!.isEmpty()) {
                    // if no item is added in filtered list we are
                    // displaying a toast message as no data found.
                    _foundData.value = false
                }
            }
        }
    }

    fun onTodoClicked(todo: Todo) {
        // TODO: Set the todo object
        _todo.value = todo
    }

    fun setKeyword(keyword: String) {
        _keyword.value = keyword
    }

    fun initKeyword() {
        _keyword.value = null
    }

    fun initTodo() {
        _todo.value = null
    }
}