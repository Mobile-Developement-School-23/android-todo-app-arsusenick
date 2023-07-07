package com.example.authorisation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authorisation.SharedPreferencesHelper
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.data.rep.TodoItemsRepository
import com.example.authorisation.internetThings.InternetConnection
import com.example.authorisation.internetThings.StateLoad
import com.example.authorisation.internetThings.internetConnection.ConnectivityObserver
import com.example.authorisation.internetThings.internetConnection.NetworkConnectivityObserver
import com.example.authorisation.internetThings.network.NetworkAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class MyViewModel(
    private val repository: TodoItemsRepository,
    private val connection: NetworkConnectivityObserver
) : ViewModel() {

    var modeAll: Boolean = false

    private val _status = MutableStateFlow(ConnectivityObserver.Status.Unavailable)
    val status = _status.asStateFlow()

    private val _data = MutableSharedFlow<List<TodoItem>>()
    val data: SharedFlow<List<TodoItem>> = _data.asSharedFlow()
    val countComplete: Flow<Int> = _data.map { it.count { item -> item.done } }

    private val _loading = MutableStateFlow<StateLoad<Any>>(StateLoad.Success("data"))
    val loading: StateFlow<StateLoad<Any>> = _loading.asStateFlow()


    private var _item = MutableStateFlow(TodoItem())
    var item = _item.asStateFlow()

    private var job: Job? = null

    init {
        observeNetwork()
        loadData()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            connection.observe().collectLatest {
                _status.emit(it)
            }
        }
    }

    fun changeMode() {
        modeAll = !modeAll
        job?.cancel()
        loadData()
    }


    fun loadData() {
        job = viewModelScope.launch(Dispatchers.IO) {
            _data.emitAll(repository.getAllData())
        }
    }


    fun getItem(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _item.value = repository.getItem(id)
        }
    }

    fun nullItem() {
        _item.value = TodoItem()
    }


    fun addItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addItem(todoItem)
        }
    }

    fun deleteItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(todoItem)
        }
    }

    fun updateItem(todoItem: TodoItem) {
        todoItem.dateChanged?.time = System.currentTimeMillis()
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeItem(todoItem)
        }
    }


    fun changeItemDone(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeDone(todoItem.id, !todoItem.done)
        }
    }

    fun loadNetworkList() {
        if (status.value == ConnectivityObserver.Status.Available) {
            _loading.value = StateLoad.Loading(true)
            viewModelScope.launch(Dispatchers.IO) {
                _loading.emit(repository.getNetworkData())
            }
        }
    }

    fun uploadNetworkItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.postNetworkItem(todoItem)
        }
    }

    fun deleteNetworkItem(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNetworkItem(id)
        }
    }

    fun updateNetworkItem(todoItem: TodoItem) {
        val item = todoItem.copy(done = !todoItem.done)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNetworkItem(item)
        }
    }


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }


}