package com.example.authorisation.data.rep

import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.data.dataBase.TodoItemDatabase
import com.example.authorisation.data.dataBase.TodoItemEnt
import com.example.authorisation.data.dataBase.TodoListDao
import com.example.authorisation.internetThings.NetworkSource
import com.example.authorisation.internetThings.StateLoad
import com.example.authorisation.internetThings.network.BaseUrl
import com.example.authorisation.internetThings.network.UiState
import com.example.authorisation.internetThings.network.responces.TODOItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TodoItemsRepository @Inject constructor(
    private val todoItemDao: TodoListDao,
    private val networkSource: NetworkSource
): RepInterface{
    override fun getAllData(): Flow<UiState<List<TodoItem>>> = flow {
        emit(UiState.Start)
        todoItemDao.getAllFlow().collect { list ->
            emit(UiState.Success(list.map { it.toItem() }))
        }
    }

    override suspend fun addItem(todoItem: TodoItem){
        val toDoItemEntity = TodoItemEnt.fromItem(todoItem)
        todoItemDao.add(toDoItemEntity)
        networkSource.postElement(todoItem)
    }

    override suspend fun deleteItem(todoItem: TodoItem) {
        val toDoItemEntity = TodoItemEnt.fromItem(todoItem)
        todoItemDao.delete(toDoItemEntity)
        networkSource.deleteElement(todoItem.id)
    }

    override suspend fun changeItem(todoItem: TodoItem){
        val toDoItemEntity = TodoItemEnt.fromItem(todoItem)
        todoItemDao.updateItem(toDoItemEntity)
        networkSource.updateElement(todoItem)
    }


    override fun getNetworkTasks(): Flow<UiState<List<TodoItem>>> = flow {
        emit(UiState.Start)
        networkSource.getMergedList(todoItemDao.getAll().map { TODOItem.fromItem(it.toItem()) })
            .collect { state ->
                when (state) {
                    StateLoad.Initial -> emit(UiState.Start)
                    is StateLoad.Exception -> emit(UiState.Error(state.cause.message.toString()))
                    is StateLoad.Result -> {
                        todoItemDao.addList(state.data.map { TodoItemEnt.fromItem(it) })
                        emit(UiState.Success(state.data))
                    }
                }
            }
    }


    override fun getItem(itemId: String): TodoItem = todoItemDao.getItem(itemId).toItem()


    override suspend fun deleteCurrentItems() {
        todoItemDao.deleteAll()
    }

}