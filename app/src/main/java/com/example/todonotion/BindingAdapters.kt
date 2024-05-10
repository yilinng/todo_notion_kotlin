package com.example.todonotion

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.todonotion.data.Keyword.Keyword
import com.example.todonotion.network.Post
import com.example.todonotion.network.Todo
import com.example.todonotion.overview.TodoApiStatus
import com.example.todonotion.overview.auth.UserApiStatus
import com.example.todonotion.ui.adapter.KeyTodoAdapter

import com.example.todonotion.ui.adapter.PostListAdapter

import com.example.todonotion.ui.adapter.TodoListAdapter

/**
 * Updates the data shown in the [RecyclerView].
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Todo>?) {
    val adapter = recyclerView.adapter as TodoListAdapter

    adapter.submitList(data)
}

@BindingAdapter("listPost")
fun bindRecyclerPostView(recyclerView: RecyclerView, data: List<Post>?) {
    val adapter = recyclerView.adapter as PostListAdapter

    adapter.submitList(data)
}

@BindingAdapter("listKeyword")
fun bindRecyclerKeywordView(recyclerView: RecyclerView, data: List<Keyword>?) {
    val adapter = recyclerView.adapter as KeyTodoAdapter
    adapter.submitList(data)
}


/**
 * Uses the Coil library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

/**
 * This binding adapter displays the [TodoApiStatus] of the network request in an image view.
 * When the request is loading, it displays a loading_animation.  If the request has an error, it
 * displays a broken image to reflect the connection error.  When the request is finished, it
 * hides the image view.
 */
@BindingAdapter("apiStatus")
fun bindStatus(statusImageView: ImageView, status: TodoApiStatus?) {
    when(status) {
        TodoApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        TodoApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        TodoApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }

        else -> {}
    }
}

/**
 * This binding adapter displays the [TodoApiStatus] of the network request in an image view.
 * When the request is loading, it displays a loading_animation.  If the request has an error, it
 * displays a broken image to reflect the connection error.  When the request is finished, it
 * hides the image view.
 */
@BindingAdapter("userApiStatus")
fun bindPostStatus(statusImageView: ImageView, status: UserApiStatus?) {
    when(status) {
        UserApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        UserApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        UserApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }

        else -> {}
    }
}






