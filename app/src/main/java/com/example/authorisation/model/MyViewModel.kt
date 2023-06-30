package com.example.authorisation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authorisation.SharedPreferencesHelper
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.data.rep.TodoItemsRepository
import com.example.authorisation.internetThings.InternetConnection
import com.example.authorisation.internetThings.network.NetworkAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class MyViewModel(
    private val rep: TodoItemsRepository,
    private val sharPref: SharedPreferencesHelper,
    private val connection: InternetConnection
): ViewModel() {

    var toggleButtMode: Boolean = false

    private val curData = MutableSharedFlow<List<TodoItem>>()
    val data: SharedFlow<List<TodoItem>> =  curData.asSharedFlow()
    val completeTask: Flow<Int> = curData.map { it.count { item -> item.done } }

    private val curTask = MutableStateFlow(TodoItem())
    var task = curTask.asStateFlow()

    private var job: Job? = null

    init {
        if(connection.isOnline()){
            loadNetworkList()
        }
        loadData()
    }

    private fun uploadNetwork(todoItem: TodoItem){
        viewModelScope.launch(Dispatchers.IO){
            when(val response = rep.postNetworkItem(sharPref.getLastRevision(), todoItem)){
                is NetworkAccess.Success -> {
                    sharPref.putRevision(response.data.revision)
                }
                is NetworkAccess.Error -> {
                    println(":(")
                }
            }
        }
    }

    private fun delItemNet(id: String){
        viewModelScope.launch(Dispatchers.IO){
            when(val response = rep.deleteNetworkItem(sharPref.getLastRevision(), id)){
                is NetworkAccess.Success -> {
                    sharPref.putRevision(response.data.revision)
                }
                is NetworkAccess.Error -> {
                    println(":(")
                }
            }
        }
    }

    private fun updateNetwork(todoItem: TodoItem){
        viewModelScope.launch(Dispatchers.IO) {
            rep.updateNetworkItem(sharPref.getLastRevision(), todoItem)
        }
    }


    fun loadNetworkList() {
        viewModelScope.launch(Dispatchers.IO) {
            rep.getNetworkData()
        }
    }

    fun loadData(){
        job = viewModelScope.launch(Dispatchers.IO){
            curData.emitAll(rep.getAllData())
        }
    }

    fun addItem(todoItem: TodoItem){
        viewModelScope.launch(Dispatchers.IO) {
            rep.addItem(todoItem)
        }
        uploadNetwork(todoItem)
    }

    fun delItem(todoItem: TodoItem){
        viewModelScope.launch(Dispatchers.IO) {
            rep.deleteItem(todoItem)
        }
        delItemNet(todoItem.id)
    }

    fun upItem(todoItem: TodoItem){
        todoItem.dateChanged?.time = System.currentTimeMillis()
        updateNetwork(todoItem)
        viewModelScope.launch(Dispatchers.IO) {
            rep.changeItem(todoItem)
        }
    }

    fun getItem(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            curTask.emit(rep.getItem(id))
        }
    }

    fun changeDoneTask(todoItem: TodoItem){
        val item = todoItem.copy(done = !todoItem.done)
        updateNetwork(item)
        viewModelScope.launch(Dispatchers.IO) {
            rep.changeDone(todoItem.id, !todoItem.done)
        }
    }

    fun loadNet(){
        viewModelScope.launch(Dispatchers.IO) {
            rep.getNetworkData()
        }
    }

    fun changeToggleMode() {
        toggleButtMode = !toggleButtMode
        job?.cancel()
        loadData()
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}