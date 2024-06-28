package com.example.todonotion.ui.adapter

import android.util.Log
import com.example.todonotion.data.local.keyword.Keyword
import com.example.todonotion.databinding.ListKeyItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class KeyTodoAdapter(
    private val onItemClicked: (Keyword, action: String) -> Unit,
    function: () -> Unit
) : ListAdapter<Keyword, KeyTodoAdapter.KeyTodoViewHolder>(DiffCallback) {

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyTodoViewHolder {

        return KeyTodoViewHolder(
            ListKeyItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )

    }

    override fun onBindViewHolder(holder: KeyTodoViewHolder, position: Int) {
        val current = getItem(position)

        //click the row
        /*
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        */
        // checkbox click
        //https://stackoverflow.com/questions/67767753/how-can-i-access-checkbox-view-in-a-recyclerview-adapter-class-from-a-layout-fil
        /*
        holder.binding.completeCheckbox.setOnClickListener {
            onItemClicked(current)
        }
        */

        holder.binding.keywordName.setOnClickListener {
            Log.d("keyword_item_name", it.toString())
            onItemClicked(current, "keyName")
        }

        holder.binding.closeKeywordBtn.setOnClickListener {
            Log.d("keyword_item_close", it.toString())
            onItemClicked(current, "close")
        }

        holder.bind(current)
    }

    //https://github.com/google-developer-training/android-basics-kotlin-inventory-app/blob/main/app/src/main/java/com/example/inventory/ItemListAdapter.kt
    //https://developer.android.com/develop/ui/views/components/checkbox
    class KeyTodoViewHolder(var binding: ListKeyItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(keyword: Keyword) {
            binding.keywordName.text = keyword.keyName
          //  binding.completeCheckbox.isChecked = keyword.isCompleted
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of
     * [Keyword] has been updated.
     */

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Keyword>() {
            override fun areItemsTheSame(oldItem: Keyword, newItem: Keyword): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Keyword, newItem: Keyword): Boolean {
                return oldItem.keyName == newItem.keyName
            }
        }
    }
}

class KeywordListener(val clickListener: (keyword: Keyword) -> Unit) {
    fun onClick(keyword: Keyword) = clickListener(keyword)
}



