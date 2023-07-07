package com.example.authorisation.data.rep

import android.content.Context
import android.util.Log
import com.example.authorisation.SharedPreferencesHelper
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.data.dataBase.TodoItemDatabase
import com.example.authorisation.data.dataBase.TodoItemEnt
import com.example.authorisation.internetThings.StateLoad
import com.example.authorisation.internetThings.network.BaseUrl
import com.example.authorisation.internetThings.network.NetworkAccess
import com.example.authorisation.internetThings.network.responces.PatchListAPI
import com.example.authorisation.internetThings.network.responces.PostResponse
import com.example.authorisation.internetThings.network.responces.PostRequest
import com.example.authorisation.internetThings.network.responces.TODOItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

//Репозиторий с захардкодиными значениями -
class TodoItemsRepository(
    todoItemsDB: TodoItemDatabase,
    private val sharedPreferencesHelper: SharedPreferencesHelper) {

    private val itemDao = todoItemsDB.todoItemDao
    fun getAllData(): Flow<List<TodoItem>> =
        itemDao.getAllFlow().map { list -> list.map { it.toItem() } }

    fun getItem(itemId: String): TodoItem = itemDao.getItem(itemId).toItem()

    suspend fun addItem(todoItem: TodoItem) {
        val toDoItemEntity = TodoItemEnt.fromItem(todoItem)
        return itemDao.add(toDoItemEntity)
    }

    suspend fun deleteItem(todoItem: TodoItem) {
        val toDoItemEntity = TodoItemEnt.fromItem(todoItem)
        return itemDao.delete(toDoItemEntity)
    }

    suspend fun changeItem(todoItem: TodoItem) {
        val toDoItemEntity = TodoItemEnt.fromItem(todoItem)
        return itemDao.updateItem(toDoItemEntity)
    }

    suspend fun changeDone(id: String, done: Boolean) {
        return itemDao.updateDone(id, done, System.currentTimeMillis())
    }

    private val service = BaseUrl.retrofitService

    suspend fun getNetworkData(): StateLoad<Any> {
        try {
            val networkListResponse = service.getList()
            if (networkListResponse.isSuccessful) {
                val body = networkListResponse.body()
                if (body != null) {
                    val revision = body.revision
                    val networkList = body.list
                    val currentList = itemDao.getAll().map { TODOItem.fromItem(it.toItem()) }
                    val mergedList = HashMap<String, TODOItem>()

                    for (item in currentList) {
                        mergedList[item.id] = item
                    }
                    for (item in networkList) {
                        if (mergedList.containsKey(item.id)) {
                            val item1 = mergedList[item.id]
                            if (item.dateChanged > item1!!.dateChanged) {
                                mergedList[item.id] = item
                            } else {
                                mergedList[item.id] = item1
                            }
                        } else if (revision != sharedPreferencesHelper.getLastRevision()) {
                            mergedList[item.id] = item
                        }
                    }

                    return updateNetworkList(mergedList.values.toList())
                }
            } else {
                networkListResponse.errorBody()?.close()
            }
        } catch (exception: Exception) {
            return StateLoad.Error("Merge failed, offline.")
        }
        return StateLoad.Error("Merge failed, offline.")

    }

    private suspend fun updateNetworkList(mergedList: List<TODOItem>): StateLoad<Any> {

        try {
            val updateResponse = service.updateList(
                sharedPreferencesHelper.getLastRevision(),
                PatchListAPI(mergedList)
            )


            if (updateResponse.isSuccessful) {
                val responseBody = updateResponse.body()
                if (responseBody != null) {
                    sharedPreferencesHelper.putRevision(responseBody.revision)
                    updateRoom(responseBody.list)
                    return StateLoad.Success(responseBody.list)
                }
            } else {
                updateResponse.errorBody()?.close()
            }
        } catch (err: Exception) {
            return StateLoad.Error("Merge failed, offline.")
        }
        return StateLoad.Error("Merge failed, offline.")
    }

    private suspend fun updateRoom(mergedList: List<TODOItem>) {
        itemDao.addList(mergedList.map { TodoItemEnt.fromItem(it.toItem()) })
    }

    suspend fun postNetworkItem(
        newItem: TodoItem
    ) {
        try {
            val postResponse = service.postElement(
                sharedPreferencesHelper.getLastRevision(),
                PostRequest(TODOItem.fromItem(newItem))
            )

            if (postResponse.isSuccessful) {
                val responseBody = postResponse.body()
                if (responseBody != null) {
                    sharedPreferencesHelper.putRevision(responseBody.revision)
                }
            } else {
                postResponse.errorBody()?.close()
            }
        } catch (err: Exception) {
            Log.d("1", err.message.toString())
        }
    }

    suspend fun deleteNetworkItem(
        id: String
    ) {
        try {
            val postResponse = service.deleteElement(id, sharedPreferencesHelper.getLastRevision())

            if (postResponse.isSuccessful) {
                val responseBody = postResponse.body()
                if (responseBody != null) {
                    sharedPreferencesHelper.putRevision(responseBody.revision)
                }
            } else {
                postResponse.errorBody()?.close()
            }
        } catch (err: Exception) {
            Log.d("1", err.message.toString())
        }
    }

    suspend fun updateNetworkItem(
        item: TodoItem
    ) {
        try {
            val updateItemResponse = service.updateElement(
                item.id, sharedPreferencesHelper.getLastRevision(), PostRequest(
                    TODOItem.fromItem(item)
                )
            )
            if (updateItemResponse.isSuccessful) {
                val body = updateItemResponse.body()
                if (body != null) {
                    sharedPreferencesHelper.putRevision(body.revision)
                }
            } else {
                updateItemResponse.errorBody()?.close()
            }
        } catch (err: Exception) {
            Log.d("1", "err")
        }
    }

    suspend fun deleteAll() {
        itemDao.deleteAll()
    }

}