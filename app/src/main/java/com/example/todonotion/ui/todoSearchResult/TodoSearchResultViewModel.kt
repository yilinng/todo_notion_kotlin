package com.example.todonotion.ui.todoSearchResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todonotion.data.TodosRepository
import com.example.todonotion.model.Todo
import javax.inject.Inject

class TodoSearchResultViewModel@Inject constructor(val todosRepository: TodosRepository) : ViewModel() {

    private val _todos = MutableLiveData<List<Todo>>()
    val todos: LiveData<List<Todo>> = _todos

    private val _todo = MutableLiveData<Todo>()
    val todo: LiveData<Todo> = _todo

    private val _filteredTodos = MutableLiveData<List<Todo>>()
    val filteredTodos: LiveData<List<Todo>> = _filteredTodos

    fun onTodoClicked(todo: Todo) {
        // TODO: Set the todo object
        _todo.value = todo
    }
}