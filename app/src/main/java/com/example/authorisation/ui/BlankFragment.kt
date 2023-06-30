package com.example.authorisation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.databinding.FragmentBlankBinding
import com.example.authorisation.model.MyViewModel
import com.example.authorisation.model.factory
import com.example.authorisation.recyclerview.DealAdapter
import com.example.authorisation.recyclerview.ItemListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


//фрагмент основного экрана

class BlankFragment : Fragment() {
    private var binding: FragmentBlankBinding? = null
    private val viewModel: MyViewModel by activityViewModels{factory()}
    private val adapter: DealAdapter? get() = views { tasks.adapter as DealAdapter }


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

            tasks.adapter = DealAdapter(object : ItemListener {
                override fun onItemClick(id: String) {
                    val action = TaskFragDirection.actionManageTask(id = id)
                    findNavController().navigate(action)
                }

                override fun onCheckClick(todoItem: TodoItem) {
                    viewModel.changeDoneTask(todoItem)
                }

            })
            eye.setOnClickListener{
                viewModel.changeToggleMode()
            }

            pullToRefresh.setOnRefreshListener {
                viewModel.loadNetworkList()
                pullToRefresh.isRefreshing = false
            }
        }

        lifecycleScope.launch {
            viewModel.data.collectLatest {
                updateUI(it)
            }
        }

        lifecycleScope.launch {
            viewModel.completeTask.collectLatest {
                updateCounter(it)
            }
        }
    }
    private fun updateCounter(count: Int) {
        views {
            titleTemplate.text = "Выполнено - $count"
        }
    }
    private fun updateUI(list: List<TodoItem>) {
        if(viewModel.toggleButtMode) {
            adapter?.submitList(list)
        }else{
            adapter?.submitList(list.filter { !it.done })
        }
    }
    private fun <T: Any> views(block: FragmentBlankBinding.() -> T): T? = binding?.block()
}