package com.example.authorisation.recyclerview.domain

import androidx.recyclerview.widget.DiffUtil
import com.example.authorisation.data.dataBase.TodoItem

class CommonCallbackImpl : DiffUtil.ItemCallback<TodoItem>(){
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean = oldItem == newItem

}