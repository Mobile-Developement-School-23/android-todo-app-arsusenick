package com.example.authorisation.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.authorisation.MyItemRecyclerViewAdapter
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.recyclerview.domain.CommonCallbackImpl

interface ItemListener{
    fun onItemClick(id: String)
    fun onCheckClick(todoItem: TodoItem)
}

class DealAdapter(
    private val onItemListener: ItemListener):
    ListAdapter<TodoItem, ViewHolder>(CommonCallbackImpl()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemListener)
    }
    }