package com.example.authorisation.model

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.authorisation.App
import com.example.authorisation.data.rep.TodoItemsRepository
import com.example.authorisation.internetThings.internetConnection.NetworkConnectivityObserver
import com.example.authorisation.internetThings.locale
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val todoItemRepository: TodoItemsRepository,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            MyViewModel::class.java -> {
                MyViewModel(todoItemRepository, connectivityObserver)
            }

            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }

}