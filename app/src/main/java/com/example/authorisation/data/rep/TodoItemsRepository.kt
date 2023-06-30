package com.example.authorisation.data.rep

import android.content.Context
import android.util.Log
import com.example.authorisation.SharedPreferencesHelper
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.data.dataBase.TodoItemDatabase
import com.example.authorisation.data.dataBase.TodoItemEnt
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

//    private var currentTask: TodoItemEnt? = null
//
//    private var eyeIsVisibility = true
//
//    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

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

    suspend fun getNetworkData() {
        val networkListResponse = service.getList()


        if (networkListResponse.isSuccessful) {
            val body = networkListResponse.body()
            if (body != null) {
                val networkList = body.list
                val currentList = itemDao.getAll().map { TODOItem.fromItem(it.toItem()) }
                val mergedList = HashMap<String, TODOItem>()

                for(item in networkList){
                    mergedList[item.id] = item
                    Log.d("1", "${item.id} ${item.dateChanged}")
                }
                for (item in currentList) {
                    if (mergedList.containsKey(item.id)) {
                        val item1 = mergedList[item.id]
                        if (item.dateChanged > item1!!.dateChanged) {
                            mergedList[item.id] = item
                        }else{
                            mergedList[item.id] = item1
                        }
                    }else{
                        mergedList[item.id] = item
                    }
                }

                updateNetworkList(mergedList.values.toList())
            }
        }
    }

    private suspend fun updateNetworkList(mergedList: List<TODOItem>) {

        val updateResponse = service.updateList(
            sharedPreferencesHelper.getLastRevision(),
            PatchListAPI(mergedList)
        )


        if (updateResponse.isSuccessful) {
            val responseBody = updateResponse.body()
            if (responseBody != null) {
                sharedPreferencesHelper.putRevision(responseBody.revision)
                updateRoom(responseBody.list)
            }
        }
    }

    private suspend fun updateRoom(response: List<TODOItem>) {
        val list = response.map { it.toItem() }
        itemDao.addList(list.map { TodoItemEnt.fromItem(it) })
    }

    suspend fun postNetworkItem(
        lastRevision: Int,
        newItem: TodoItem
    ): NetworkAccess<PostResponse> {
        val postResponse = service.postElement(
            lastRevision,
            PostRequest(TODOItem.fromItem(newItem))
        )

        if (postResponse.isSuccessful) {
            val responseBody = postResponse.body()
            if (responseBody != null) {
                return NetworkAccess.Success(responseBody)
            }
        }
        return NetworkAccess.Error(postResponse)
    }

    suspend fun deleteNetworkItem(
        lastRevision: Int,
        id: String
    ): NetworkAccess<PostResponse> {
        val postResponse = service.deleteElement(id, lastRevision)

        if (postResponse.isSuccessful) {
            val responseBody = postResponse.body()
            if (responseBody != null) {
                return NetworkAccess.Success(responseBody)
            }
        }
        return NetworkAccess.Error(postResponse)
    }

    suspend fun updateNetworkItem(
        lastRevision: Int,
        item: TodoItem
    ) = withContext(Dispatchers.IO) {

        val updateItemResponse = service.updateElement(
            item.id, lastRevision, PostRequest(
                TODOItem.fromItem(item)
            )
        )
        if (updateItemResponse.isSuccessful) {
            val body = updateItemResponse.body()
            if (body != null) {
                sharedPreferencesHelper.putRevision(body.revision)
            }
        }
    }

//    fun getTasks(context: Context?): List<TaskPreview>{
//        TODO()
////        eturn buildList {
////            val date = "10/07/2023"
////            val text = "Скушать булочку, попить водичку, ММММ блаженство"
////            val text1 = "Cходить в магазин"
////            val text2 = "Узнать как можно купить машину не покупая машину и при это остаться при деньгах и на свободе"
////            val text3 = "Посмотреть телевизор"
////            val text4 = "Скушать сгущенку и яблоко, и конфеты, и ботинки, и собаку, и кошку, окрошку, сережку, максимку, димку раунд..."
////
////            add(
////                TaskPreview("0",1,text,
////                date, true, date, null)
////            )
////
////            add(
////                TaskPreview("1",1,text,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("2",2,text1,
////            null, true, date, null)
////            )
////
////            add(
////                TaskPreview("3",0,text2,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("4",1,text3,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("5",1,text4,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("6",2,text2,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("7",1,text4,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("8",0,text,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("9",0,text1,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("10",2,text3,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("11",0,text,
////                null, false, date, null)
////            )
////
////            add(
////                TaskPreview("12",1,text4,
////                null, false, date, null)
////            )
////
////            add(
////                TaskPreview("13",2,text2,
////                null, false, date, null)
////            )
////
////            add(
////                TaskPreview("14",0,text,
////                null, false, date, null)
////            )
////
////            add(
////                TaskPreview("15",1,text3,
////                null, true, date, null)
////            )
////
////            add(
////                TaskPreview("end",3,text1,
////                null, false, date, null)
////            )
////
////
////        }
//    }
}