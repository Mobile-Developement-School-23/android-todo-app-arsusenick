package com.example.authorisation.data.rep

import android.content.Context
import android.util.Log
import com.example.authorisation.SharedPreferencesHelper
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.data.dataBase.TodoItemDatabase
import com.example.authorisation.data.dataBase.TodoItemEnt
import com.example.authorisation.internetThings.NetworkSource
import com.example.authorisation.internetThings.StateLoad
import com.example.authorisation.internetThings.network.BaseUrl
import com.example.authorisation.internetThings.network.NetworkAccess
import com.example.authorisation.internetThings.network.UiState
import com.example.authorisation.internetThings.network.responces.PatchListAPI
import com.example.authorisation.internetThings.network.responces.PostResponse
import com.example.authorisation.internetThings.network.responces.PostRequest
import com.example.authorisation.internetThings.network.responces.TODOItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

//Репозиторий с захардкодиными значениями -
class TodoItemsRepository(
    database: TodoItemDatabase,
    private val networkSource: NetworkSource
): RepInterface{
    private val itemDao = database.todoItemDao
    override fun getAllData(): Flow<UiState<List<TodoItem>>> = flow {
        emit(UiState.Start)
        itemDao.getAllFlow().collect { list ->
            emit(UiState.Success(list.map { it.toItem() }))
        }
    }

    override fun getItem(itemId: String): TodoItem = itemDao.getItem(itemId).toItem()

    override suspend fun addItem(todoItem: TodoItem) {
        val toDoItemEntity = TodoItemEnt.fromItem(todoItem)
        return itemDao.add(toDoItemEntity)
    }

    override suspend fun deleteItem(todoItem: TodoItem) {
        val toDoItemEntity = TodoItemEnt.fromItem(todoItem)
        return itemDao.delete(toDoItemEntity)
    }

    override suspend fun changeItem(todoItem: TodoItem) {
        val toDoItemEntity = TodoItemEnt.fromItem(todoItem)
        return itemDao.updateItem(toDoItemEntity)
    }

    suspend fun changeDone(id: String, done: Boolean) {
        return itemDao.updateDone(id, done, System.currentTimeMillis())
    }

    private val service = BaseUrl.retrofitService

    override fun getNetworkTasks(): Flow<UiState<List<TodoItem>>> = flow {
        emit(UiState.Start)
        networkSource.getMergedList(itemDao.getAll().map { TODOItem.fromItem(it.toItem()) })
            .collect { state ->
                when (state) {
                    StateLoad.Initial -> emit(UiState.Start)
                    is StateLoad.Exception -> emit(UiState.Error(state.cause.message.toString()))
                    is StateLoad.Result -> {
                        updateRoom(state.data)
                        emit(UiState.Success(state.data))
                    }
                }
            }
    }

    private suspend fun updateRoom(mergedList: List<TodoItem>) {
        itemDao.addList(mergedList.map { TodoItemEnt.fromItem(it) })
    }

    override suspend fun postNetworkItem(
        newItem: TodoItem
    ) {
        networkSource.postElement(newItem)
    }

    override suspend fun deleteNetworkItem(
        id: String
    ) {
        networkSource.deleteElement(id)
    }

    override suspend fun updateNetworkItem(
        item: TodoItem
    ) {
        networkSource.updateElement(item)
    }

    override suspend fun deleteAll() {
        itemDao.deleteAll()
    }

}