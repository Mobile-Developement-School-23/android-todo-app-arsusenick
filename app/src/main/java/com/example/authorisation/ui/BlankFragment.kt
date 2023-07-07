package com.example.authorisation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.authorisation.R
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.databinding.FragmentBlankBinding
import com.example.authorisation.internetThings.StateLoad
import com.example.authorisation.internetThings.internetConnection.ConnectivityObserver
import com.example.authorisation.model.MyViewModel
import com.example.authorisation.model.factory
import com.example.authorisation.recyclerview.DealsAdapter
import com.example.authorisation.recyclerview.ItemListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


//фрагмент основного экрана

class BlankFragment : Fragment() {
    private var binding: FragmentBlankBinding? = null
    private val viewModel: MyViewModel by activityViewModels{factory()}
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
                    if (internetState == ConnectivityObserver.Status.Available) {
                        viewModel.updateNetworkItem(todoItem)
                    } else {
                        Toast.makeText(
                            context,
                            "No internet connection, will upload with later. Continue offline.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.changeItemDone(todoItem)
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
            viewModel.data.collect {
                updateRecyclerData(it)
            }
        }
        lifecycleScope.launch {
            viewModel.countComplete.collectLatest {
                updateCounter(it)
            }
        }

        lifecycleScope.launch {
            viewModel.loading.collectLatest {
                updateLoadingUI(it)
            }
        }
    }
    private fun updateCounter(count: Int) {
        views {
            titleTemplate.text = "Выполнено - $count"
        }
    }

    private fun updateLoadingUI(loadingState: StateLoad<Any>) {
        when (loadingState) {
            is StateLoad.Loading -> {
                views {
                    tasks.visibility = View.GONE
                }
            }

            is StateLoad.Success -> {
                views {
                    tasks.visibility = View.VISIBLE
                }
            }

            is StateLoad.Error -> {
                views {
                    tasks.visibility = View.VISIBLE
                }
                Toast.makeText(
                    context,
                    loadingState.error,
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun updateRecyclerData(list: List<TodoItem>) {
        if (viewModel.modeAll) {
            adapter?.submitList(list)
        } else {
            adapter?.submitList(list.filter { !it.done })
        }
    }
    private fun <T: Any> views(block: FragmentBlankBinding.() -> T): T? = binding?.block()
}