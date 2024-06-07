package com.example.todonotion.ui.todoSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.TodosRepository
import com.example.todonotion.model.Todo
import com.example.todonotion.overview.TodoApiStatus
import kotlinx.coroutines.launch
import javax.inject.Inject


class TodoSearchViewModel @Inject constructor(val todosRepository: TodosRepository) : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<TodoApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<TodoApiStatus> = _status


    private val _foundData = MutableLiveData<Boolean>()
    private val foundData: LiveData<Boolean> = _foundData


    private val _filteredTodos = MutableLiveData<List<Todo>>()
    private val filteredTodos: LiveData<List<Todo>> = _filteredTodos

    fun getTodoPhotosByKeyWord(string: String) {
        viewModelScope.launch {
            _status.value = TodoApiStatus.LOADING
            try {
                _filteredTodos.value = todosRepository.getPhotosWithKey(string).hits
                _status.value = TodoApiStatus.DONE
               // Log.i("getTodoPhotosByKeyWord200", todos.toString())

            } catch (e: Exception) {
                _status.value = TodoApiStatus.ERROR
                _filteredTodos.value = listOf()
               // Log.e("getTodoPhotosByKeyWord404", e.toString())
            } finally {
                if (filteredTodos.value!!.isEmpty()) {
                    // if no item is added in filtered list we are
                    // displaying a toast message as no data found.
                    _foundData.value = false
                }
            }
        }
    }
}