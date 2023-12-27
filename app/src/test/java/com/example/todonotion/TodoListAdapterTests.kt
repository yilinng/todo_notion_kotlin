package com.example.todonotion

import android.content.Context
import com.example.todonotion.network.Todo
import com.example.todonotion.ui.adapter.TodoListAdapter
import com.example.todonotion.ui.adapter.TodoListener
import junit.framework.Assert.assertEquals

import org.junit.Test
import org.mockito.Mockito.mock

class TodoListAdapterTests {

    private val context = mock(Context::class.java)
    /*
      @Test

      fun adapter_size(){
          val todoList = listOf(
              Todo(id="8455782", pageURL="https://pixabay.com/photos/christmas-baubles-heart-decoration-8455782/",
                  type="photo",tags="christmas baubles, heart, laptop wallpaper", views=8336,downloads=7061,collections=22,likes=103,comments=48,user_id=1425977
                  ,user="ChiemSeherin", userImageURL="https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg", imgSrcUrl="https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg"),
              Todo(id="8455782", pageURL="https://pixabay.com/photos/christmas-baubles-heart-decoration-8455782/",
                  type="photo",tags="christmas baubles, heart, laptop wallpaper", views=8336,downloads=7061,collections=22,likes=103,comments=48,user_id=1425977
                  ,user="ChiemSeherin", userImageURL="https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg", imgSrcUrl="https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg")
          )

          //val adapter = TodoListAdapter(TodoListener{todo: Todo ->  } )

          assertEquals("TodoListAdapter is not the correct size", todoList.size, adapter.itemCount)
      }

       */
}