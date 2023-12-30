package com.example.todonotion

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todonotion.network.Todo
import com.example.todonotion.overview.OverViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

//https://developer.android.com/codelabs/android-basics-kotlin-test-viewmodel-and-livedata?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-kotlin-unit-3-pathway-4%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fandroid-basics-kotlin-test-viewmodel-and-livedata#5
class ViewModelTests {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val todo = Todo(id="8455782", pageURL="https://pixabay.com/photos/christmas-baubles-heart-decoration-8455782/",
        type="photo",tags="christmas baubles, heart, laptop wallpaper", views=8336,downloads=7061,collections=22,likes=103,comments=48,user_id=1425977
        ,user="ChiemSeherin", userImageURL="https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg", imgSrcUrl="https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg")

    @Test
    fun check_todo_click() {
        val viewModel = OverViewModel()
        viewModel.todo.observeForever {}
        viewModel.onTodoClicked(todo)
        assertEquals(todo, viewModel.todo.value)
    }

    //https://stackoverflow.com/questions/49626315/how-to-select-a-specific-tab-position-in-tab-layout-using-espresso-testing

    /*
    @Test
    fun check_select_tab() {
        val viewModel = OverViewModel()
        viewModel.todos.observeForever {}
        viewModel.filteredTodos.observeForever {}

        val tab = TabLayout.Tab()

        tab.orCreateBadge

        viewModel.selectedTab(tab)
        assertEquals(viewModel.todos.value!!.size, viewModel.filteredTodos.value!!.size)
    }
    */
    /*
    @Test
    fun price_twelve_cupcakes() {
        val viewModel = OverViewModel()
        viewModel.setQuantity(12)
        viewModel.price.observeForever {}
        assertEquals("$27.00", viewModel.price.value)
    }
     */
}

//https://stackoverflow.com/questions/58303961/kotlin-coroutine-unit-test-fails-with-module-with-the-main-dispatcher-had-faile
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}