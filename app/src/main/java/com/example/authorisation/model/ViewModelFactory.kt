package com.example.authorisation.model

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.authorisation.App
import com.example.authorisation.internetThings.locale

class ViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            MyViewModel::class.java -> {
                MyViewModel(locale(), locale())
            }

            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }

}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)