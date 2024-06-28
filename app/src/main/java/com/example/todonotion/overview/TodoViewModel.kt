package com.example.todonotion.overview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.TodosRepository
import com.example.todonotion.data.model.Todo

import java.util.*

import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch


enum class TodoApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [TodoListFragment].
 */
class TodoViewModel(val todosRepository: TodosRepository) : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<TodoApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<TodoApiStatus> = _status

    private val _todos = MutableLiveData<List<Todo>>()
    val todos: LiveData<List<Todo>> = _todos

    private val _todo = MutableLiveData<Todo>()
    val todo: LiveData<Todo> = _todo

    private val _foundData = MutableLiveData<Boolean>()
    private val foundData: LiveData<Boolean> = _foundData


    private val _filteredTodos = MutableLiveData<List<Todo>>()
    val filteredTodos: LiveData<List<Todo>> = _filteredTodos

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getTodoPhotos()
    }

    /**
     * Gets photos information from the pixabay API Retrofit service and updates the
     * [Todo] [List] [LiveData].
     */
    private fun getTodoPhotos() {
        viewModelScope.launch {
            _status.value = TodoApiStatus.LOADING
            try {
                _todos.value = todosRepository.getPhotos().hits
                _status.value = TodoApiStatus.DONE
                Log.i("getTodoPhotos200", status.toString())

            } catch (e: Exception) {
                _status.value = TodoApiStatus.ERROR
                _todos.value = listOf()
                Log.e("getTodoPhotos404", TodoApiStatus.ERROR.toString())

            }finally {
                if (todos.value!!.isEmpty()) {
                    // if no item is added in filtered list we are
                    // displaying a toast message as no data found.
                    _foundData.value = false
                }
            }
        }
    }


    /**
     * Gets photos information from the pixabay API Retrofit service and updates the
     * [Todo] [List] [LiveData].
     */
    fun getTodoPhotosByKeyWord(string: String) {
        viewModelScope.launch {
            _status.value = TodoApiStatus.LOADING
            try {
                _filteredTodos.value = todosRepository.getPhotosWithKey(string).hits
                _status.value = TodoApiStatus.DONE
                Log.i("getTodoPhotosByKeyWord200", todos.toString())

            } catch (e: Exception) {
                _status.value = TodoApiStatus.ERROR
                _filteredTodos.value = listOf()
                Log.e("getTodoPhotosByKeyWord404", e.toString())
            } finally {
                if (filteredTodos.value!!.isEmpty()) {
                    // if no item is added in filtered list we are
                    // displaying a toast message as no data found.
                    _foundData.value = false
                }
            }
        }
    }

    //when tab selected
    fun selectedTab(tab: TabLayout.Tab?){
        if (tab != null) {
            //Log.d("selectedTab", tab.text.toString())
            if(tab.text == "Recent") {
                //use first get todos
                getTodoPhotos()
            }else {
                //store last filtered todos
                _todos.value = filteredTodos.value
            }
        }
    }

    /*
    fun splitTags(todo: Todo): List<String> {
        val stars = todo.tags.split(",").toTypedArray()
        return stars.toList()
    }
    */
    /*
    fun filterByTags(text: String){
        //creating a new array list to filter our data
        val filteredList = ArrayList<Todo>()

        // running a for loop to compare elements.
        for (item in todos.value!!) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.tags.lowercase().contains(text.lowercase(Locale.getDefault()))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item)
                _foundData.value = true
            }
        }

        _filteredTodos.value = filteredList

        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            _foundData.value = false
        }

    }
    */

    fun dataNotFound(): String {
        return if (foundData.value == false || todos.value?.isEmpty() == true) {
            "data not found."
        } else {
            ""
        }
    }

    //https://stackoverflow.com/questions/46662513/convert-array-to-list-in-kotlin
    fun onTodoClicked(todo: Todo) {
        // TODO: Set the todo object
        _todo.value = todo
    }

    /**
     * Factory for [TodoViewModel] that takes [TodosRepository] as a dependency

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BaseApplication)
                val todosRepository = application.container.todosRepository
                TodoViewModel(todosRepository = todosRepository)
            }
        }
    }
     */

}

