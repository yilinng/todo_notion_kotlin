package com.example.todonotion.ui.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todonotion.databinding.ListItemPostBinding
import com.example.todonotion.data.model.Post

import java.time.Duration
import java.time.LocalDateTime


/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class PostListAdapter(private val clickListener: PostListener) :
    ListAdapter<Post, PostListAdapter.PostViewHolder>(DiffCallback) {

    /**
     * The PostViewHolder constructor takes the binding variable from the associated
     * GridViewItem, which nicely gives it access to the full [Post] information.
     */
    // method for filtering our recyclerview items.
    fun filterList() {
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    class PostViewHolder(
        private var binding: ListItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(clickListener: PostListener, post: Post) {
            binding.post = post
            binding.title.text = stringSlice(post.title)
            binding.time.text = post.updateDate?.let { datetimeConvert(it) }
            binding.clickListener = clickListener
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of
     * [Post] has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Post>() {

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.title == newItem.title && oldItem.user == newItem.user
        }

    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostViewHolder(
            ListItemPostBinding.inflate(layoutInflater, parent, false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(clickListener, post)
    }


}

fun stringSlice(str: String): String {
    return if (str.length > 30) {
        str.substring(0, 30) + "..."
    } else {
        str
    }
}

//https://stackoverflow.com/questions/58285591/how-to-convert-hours-to-minutes-in-kotlin-and-pass-correctly-to-viewholder
@RequiresApi(Build.VERSION_CODES.O)
fun datetimeConvert(date: String): String {
    val lastTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.parse(date.substring(0, 19))
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    val nowTime = LocalDateTime.now()
    val elapsedTime = Duration.between(lastTime, nowTime)

    var dateStr = ""

    if (elapsedTime.toDays() > 0) {
        dateStr = dateStr + elapsedTime.toDays() + " days "
    } else if (elapsedTime.toHours() > 0) {
        dateStr = dateStr + elapsedTime.toHours() + " hours "
    } else if (elapsedTime.toHours() < 1) {
        dateStr = dateStr + elapsedTime.toMinutes() + " mins "
    }

    // var dateStr = elapsedTime.toString().split(".")

    // var dateMStr = dateStr[0].substring(2).split("M")

    Log.d("convertTime", elapsedTime.toString())

    return dateStr + "ago"
}


class PostListener(val clickListener: (post: Post) -> Unit) {
    fun onClick(post: Post) = clickListener(post)
}
