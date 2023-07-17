package com.example.authorisation.ui.swipe

import com.example.authorisation.data.dataBase.TodoItem

interface SwipeInterface {
    fun onDelete(todoItem: TodoItem)
    fun onChangeDone(todoItem: TodoItem)
}