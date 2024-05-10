package com.example.todonotion.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todonotion.databinding.ListItemPostBinding
import com.example.todonotion.network.Post

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
        fun bind(clickListener: PostListener,post: Post) {
            binding.post = post
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
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(clickListener,post)
    }



}


class PostListener(val clickListener: (post: Post) -> Unit) {
    fun onClick(post: Post) = clickListener(post)
}
