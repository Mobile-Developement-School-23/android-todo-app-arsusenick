package com.example.authorisation.ui.stateHold

import androidx.lifecycle.ViewModel
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.data.rep.TodoItemsRepository
import com.example.authorisation.internetThings.network.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ManageTaskViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
    private val coroutineScope: CoroutineScope
) : ViewModel() {


    private val _todoItem:MutableStateFlow<UiState<TodoItem>> = MutableStateFlow(UiState.Start)
    val todoItem = _todoItem.asStateFlow()

    fun getItem(id:String) {
        if(todoItem.value == UiState.Start) {
            coroutineScope.launch(Dispatchers.IO) {
                _todoItem.emit(UiState.Success(repository.getItem(id)))
            }
        }
    }
    fun setItem() {
        if(todoItem.value == UiState.Start) {
            _todoItem.value = UiState.Success(TodoItem())
        }
    }

    fun addItem(todoItem: TodoItem){
        coroutineScope.launch(Dispatchers.IO) {
            repository.addItem(todoItem.copy())
        }
    }

    fun deleteItem(todoItem: TodoItem) {
        coroutineScope.launch(Dispatchers.IO) {
            repository.deleteItem(todoItem)
        }
    }

    fun updateItem(task: TodoItem) {
        task.dateChanged?.time = System.currentTimeMillis()
        coroutineScope.launch(Dispatchers.IO) {
            repository.changeItem(task)
        }
    }


}
