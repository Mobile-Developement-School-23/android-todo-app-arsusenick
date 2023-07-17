package com.example.authorisation.internetThings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.authorisation.data.rep.TodoItemsRepository
import com.example.authorisation.internetThings.internetConnection.NetworkConnectivityObserver
import com.example.authorisation.internetThings.notifications.NotificationsSchedulerImpl
import com.example.authorisation.ui.stateHold.LoginViewModel
import com.example.authorisation.ui.stateHold.ManageTaskViewModel
import com.example.authorisation.ui.stateHold.MyViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val repositoryImpl: TodoItemsRepository,
    private val connectivityObserver: NetworkConnectivityObserver,
    private val coroutineScope: CoroutineScope,
    private val schedulerImpl: NotificationsSchedulerImpl
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            MyViewModel::class.java -> {
                MyViewModel(repositoryImpl, connectivityObserver, coroutineScope)
            }
            LoginViewModel::class.java -> {
                LoginViewModel(repositoryImpl, coroutineScope, schedulerImpl)
            }

            ManageTaskViewModel::class.java -> {
                ManageTaskViewModel(repositoryImpl, coroutineScope)
            }

            else -> {
                error("Unknown view model class")
            }
        }
        return viewModel as T
    }

}
