package com.example.todonotion.ui.adapter

import android.graphics.Color
import com.example.todonotion.data.Keyword.Keyword
import com.example.todonotion.databinding.ListKeyItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class KeyTodoAdapter (
    private val onItemClicked: (Keyword) -> Unit
) : ListAdapter<Keyword, KeyTodoAdapter.KeyTodoViewHolder>(DiffCallback) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyTodoViewHolder {
        val viewHolder = KeyTodoViewHolder(
            ListKeyItemBinding.inflate(
                LayoutInflater.from( parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: KeyTodoViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(getItem(position))
    }

    class KeyTodoViewHolder(
        private var binding: ListKeyItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(keyword: Keyword) {
           // binding.stopNameTextView.text = key.keyName
            binding.keywordName.text = keyword.keyName
        }
    }
}

