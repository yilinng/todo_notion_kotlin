package com.example.todonotion.ui.todoDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.TodosRepository
import com.example.todonotion.model.Todo
import com.example.todonotion.overview.TodoApiStatus
import kotlinx.coroutines.launch
import javax.inject.Inject


class TodoDetailsViewModel @Inject constructor(val todosRepository: TodosRepository): ViewModel(){
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<TodoApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<TodoApiStatus> = _status

    private val _todo = MutableLiveData<Todo?>()
    val todo: LiveData<Todo?> = _todo

   fun getTodoPhoto(postId: String) {
        viewModelScope.launch {
            _status.value = TodoApiStatus.LOADING
            try {
                _todo.value = todosRepository.getPhotosById(postId).hits[0]
                _status.value = TodoApiStatus.DONE
                //Log.i("getTodoPhotos200", status.toString())

            } catch (e: Exception) {
                _status.value = TodoApiStatus.ERROR
                _todo.value = null
                //Log.e("getTodoPhotos404", TodoApiStatus.ERROR.toString())

            }
        }
    }
}