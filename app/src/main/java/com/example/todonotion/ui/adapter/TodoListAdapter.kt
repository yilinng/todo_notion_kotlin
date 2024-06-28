package com.example.todonotion.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todonotion.databinding.ListItemTodoBinding
import com.example.todonotion.data.model.Todo

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class TodoListAdapter(private val clickListener: TodoListener) :
    ListAdapter<Todo, TodoListAdapter.TodoViewHolder>(DiffCallback) {

    /**
     * The TodoViewHolder constructor takes the binding variable from the associated
     * GridViewItem, which nicely gives it access to the full [Todo] information.
     */
    // method for filtering our recyclerview items.
    fun filterList() {
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    class TodoViewHolder(
        private var binding: ListItemTodoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: TodoListener,todo: Todo) {
            binding.todo = todo
            binding.todoType.text = todo.type
            binding.todoTags.text = todo.tags.replace(',', ' ')
            binding.todoUser.text = todo.user
            binding.clickListener = clickListener
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of
     * [Todo] has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Todo>() {

        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
             return oldItem.tags == newItem.tags && oldItem.imgSrcUrl == newItem.imgSrcUrl
        }

    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TodoViewHolder(
            ListItemTodoBinding.inflate(layoutInflater, parent, false)
            )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(clickListener,todo)
    }

}


class TodoListener(val clickListener: (todo: Todo) -> Unit) {
    fun onClick(todo: Todo) = clickListener(todo)
}




