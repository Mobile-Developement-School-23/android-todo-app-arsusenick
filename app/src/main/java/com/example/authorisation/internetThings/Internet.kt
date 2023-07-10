package com.example.authorisation.internetThings

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.authorisation.SharedPreferencesHelper
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.internetThings.network.BaseUrl
import com.example.authorisation.internetThings.network.responces.PatchListAPI
import com.example.authorisation.internetThings.network.responces.PostRequest
import com.example.authorisation.internetThings.network.responces.TODOItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NetworkSource @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val service: RetrofitService
) {


    suspend fun getMergedList(currentList: List<TODOItem>): Flow<StateLoad<List<TodoItem>>> =
        flow {
            emit(StateLoad.Initial)
            try {
                val getResponse = service.getList()

                val revision = getResponse.revision
                val networkList = getResponse.list
                val mergedMap = HashMap<String, TODOItem>()

                for (item in currentList) {
                    mergedMap[item.id] = item
                }
                for (item in networkList) {
                    if (mergedMap.containsKey(item.id)) {
                        val item1 = mergedMap[item.id]
                        if (item.dateChanged > item1!!.dateChanged) {
                            mergedMap[item.id] = item
                        } else {
                            mergedMap[item.id] = item1
                        }
                    } else if (revision != sharedPreferencesHelper.getLastRevision()) {
                        mergedMap[item.id] = item
                    }
                }
                sharedPreferencesHelper.putRevision(revision)
                val mergedList = mergedMap.values.toList()
                val patchResponse = service.updateList(
                    sharedPreferencesHelper.getLastRevision(),
                    PatchListAPI(mergedList)
                )
                sharedPreferencesHelper.putRevision(patchResponse.revision)

                emit(StateLoad.Result(patchResponse.list.map { it.toItem() }))
            } catch (exception: Exception) {
                emit(StateLoad.Exception(exception))
            }
        }

    suspend fun postElement(item: TodoItem) {
        try {
            val postResponse = service.postElement(
                sharedPreferencesHelper.getLastRevision(),
                PostRequest(TODOItem.fromItem(item))
            )

            sharedPreferencesHelper.putRevision(postResponse.revision)
        } catch (err: Exception) {
            Log.d("1", err.message.toString())
        }
    }

    suspend fun deleteElement(id: String) {
        try {
            val deleteResponse =
                service.deleteElement(id, sharedPreferencesHelper.getLastRevision())
            sharedPreferencesHelper.putRevision(deleteResponse.revision)
        } catch (err: Exception) {
            Log.d("1", err.message.toString())
        }
    }

    suspend fun updateElement(item: TodoItem) {
        try {
            val updateItemResponse = service.updateElement(
                item.id, sharedPreferencesHelper.getLastRevision(), PostRequest(
                    TODOItem.fromItem(item)
                )
            )
            sharedPreferencesHelper.putRevision(updateItemResponse.revision)
        } catch (err: Exception) {
            Log.d("1", "err")
        }
    }
}