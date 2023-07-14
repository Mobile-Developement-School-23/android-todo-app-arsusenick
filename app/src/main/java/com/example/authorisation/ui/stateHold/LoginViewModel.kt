package com.example.authorisation.ui.stateHold

import androidx.lifecycle.ViewModel
import com.example.authorisation.data.rep.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
    private val coroutineScope: CoroutineScope
) : ViewModel() {

    fun deleteCurrentItems() {
        coroutineScope.launch(Dispatchers.IO) {
            repository.deleteCurrentItems()
        }
    }
}