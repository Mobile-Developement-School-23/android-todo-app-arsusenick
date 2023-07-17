package com.example.authorisation.data.rep

import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.internetThings.network.UiState
import kotlinx.coroutines.flow.Flow

interface RepInterface {
    fun getAllData(): Flow<UiState<List<TodoItem>>>
    fun getItem(itemId: String): TodoItem
    suspend fun addItem(todoItem: TodoItem)
    suspend fun deleteItem(todoItem: TodoItem)
    suspend fun changeItem(todoItem: TodoItem)
    fun getNetworkTasks(): Flow<UiState<List<TodoItem>>>
    suspend fun deleteCurrentItems()
}