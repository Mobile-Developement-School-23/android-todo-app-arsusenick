package com.example.authorisation.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.data.rep.TodoItemsRepository
import com.example.authorisation.internetThings.internetConnection.ConnectivityObserver
import com.example.authorisation.internetThings.internetConnection.NetworkConnectivityObserver
import com.example.authorisation.internetThings.network.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MyViewModel(
    private val repository: TodoItemsRepository,
    private val connection: NetworkConnectivityObserver
) : ViewModel() {

    private val _status = MutableStateFlow(ConnectivityObserver.Status.Unavailable)
    val status = _status.asStateFlow()

    private val _data = MutableStateFlow<UiState<List<TodoItem>>>(UiState.Start)
    val data: StateFlow<UiState<List<TodoItem>>> = _data.asStateFlow()

    val countComplete = _data.map{ state->
        when(state){
            is UiState.Success->{
                state.data.count{it.done}
            }else->{
            0
            }
        }
    }

    private val _visibility: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val visibility: StateFlow<Boolean> = _visibility

    private var _item = MutableStateFlow(TodoItem())
    var item = _item.asStateFlow()


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
        _visibility.value = _visibility.value.not()
    }


    fun loadData(){
        viewModelScope.launch(Dispatchers.IO) {
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

    fun setTask(task: TodoItem) {
        task.dateChanged?.time = System.currentTimeMillis()
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeItem(task)
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

    fun loadNetworkList(){
        viewModelScope.launch(Dispatchers.IO) {
            _data.emitAll(repository.getNetworkTasks())
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNetworkItem(todoItem)
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
            Log.d("1", "HEY")
            loadNetworkList()
        }
    }


}