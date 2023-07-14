package com.example.authorisation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.authorisation.App
import com.example.authorisation.R
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.databinding.FragmentBlankBinding
import com.example.authorisation.internetThings.internetConnection.ConnectivityObserver
import com.example.authorisation.internetThings.network.UiState

import com.example.authorisation.recyclerview.DealsAdapter
import com.example.authorisation.recyclerview.ItemListener
import com.example.authorisation.ui.stateHold.MyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


//фрагмент основного экрана

class BlankFragment : Fragment() {
    private var binding: FragmentBlankBinding? = null
    private val viewModel: MyViewModel by viewModels {
        (requireContext().applicationContext as App).appComponent.viewModelsFactory() }
    private val adapter: DealsAdapter? get() = views { tasks.adapter as DealsAdapter }

    private var internetState = ConnectivityObserver.Status.Unavailable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBlankBinding.inflate(LayoutInflater.from(context)).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadData()

        views {
            addButt.setOnClickListener{
                val action = TaskFragDirection.actionManageTask(null)
                findNavController().navigate(action)
            }

            tasks.adapter = DealsAdapter(object : ItemListener {
                override fun onItemClick(id: String) {
                    val action = TaskFragDirection.actionManageTask(id = id)
                    findNavController().navigate(action)
                }

                override fun onCheckClick(todoItem: TodoItem) {
                    viewModel.updateItem(
                        todoItem.copy(
                            done = !todoItem.done
                        )
                    )
                }

            })
            eye.setOnClickListener{
                viewModel.changeMode()
            }

            pullToRefresh.setOnRefreshListener {
                if (internetState == ConnectivityObserver.Status.Available) {
                    viewModel.loadNetworkList()
                } else {
                    Toast.makeText(
                        context,
                        "No internet connection, aoaoaooaoaooaoa(((9((9(",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                pullToRefresh.isRefreshing = false
            }
        }

        lifecycleScope.launch {
            viewModel.status.collectLatest {
                updateStatusUI(it)
            }
        }

        lifecycleScope.launch {
            viewModel.visibility.collectLatest { visibilityState ->
                updateStateUI(visibilityState)
            }
        }
        lifecycleScope.launch {
            viewModel.countComplete.collectLatest {
                updateCounter(it)
            }
        }

        internetState = viewModel.status.value
    }
    private fun updateCounter(count: Int) {
        views {
            titleTemplate.text = "Выполнено - $count"
        }
    }

    private suspend fun updateStateUI(visibilityState: Boolean) {
        viewModel.data.collect { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    if (visibilityState) {
                        adapter?.submitList(uiState.data)
                    } else {
                        adapter?.submitList(uiState.data.filter { !it.done })
                    }
                    views {
                        tasks.visibility = View.VISIBLE
                    }
                }

                is UiState.Error -> Log.d("1", uiState.cause)
                is UiState.Start -> {
                    views {
                        tasks.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun updateStatusUI(status: ConnectivityObserver.Status) {
        views {
            when (status) {
                ConnectivityObserver.Status.Available -> {
                    connection.imageTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.green)
                    if (internetState != status) {
                        Toast.makeText(context, "Connected! Merging data...", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.loadNetworkList()
                    }

                }

                ConnectivityObserver.Status.Unavailable -> {
                    connection.imageTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.red)
                    if (internetState != status) {
                        Toast.makeText(
                            context,
                            "Internet unavailable! Work offline",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.loadNetworkList()
                    }
                }

                ConnectivityObserver.Status.Losing -> {
                    connection.imageTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.orange)

                    if (internetState != status) {
                        Toast.makeText(context, "Losing Internet!", Toast.LENGTH_SHORT).show()
                    }
                }

                ConnectivityObserver.Status.Lost -> {
                    connection.imageTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.red)

                    if (internetState != status) {
                        Toast.makeText(context, "Internet Lost!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        internetState = status
    }
    private fun <T: Any> views(block: FragmentBlankBinding.() -> T): T? = binding?.block()
}