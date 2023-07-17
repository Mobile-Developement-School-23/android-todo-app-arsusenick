package com.example.authorisation.ui.view.tasks

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.authorisation.R
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.databinding.FragmentBlankBinding
import com.example.authorisation.internetThings.internetConnection.ConnectivityObserver
import com.example.authorisation.internetThings.network.UiState
import com.example.authorisation.recyclerview.DealsAdapter
import com.example.authorisation.recyclerview.ItemListener
import com.example.authorisation.ui.stateHold.MyViewModel
import com.example.authorisation.ui.swipe.SwipeHelper
import com.example.authorisation.ui.swipe.SwipeInterface
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BlankFragmentController(
    private val context: Context,
    private val navController: NavController,
    private val binding: FragmentBlankBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: MyViewModel,
    private val layoutInflater: LayoutInflater
) {
    private var internetState = viewModel.status.value
    private val adapter: DealsAdapter get() = views { tasks.adapter as DealsAdapter }

    fun setUpViews() {
        setUpUI()
        setUpViewModel()
    }

    private fun setUpViewModel() {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.status.collectLatest {
                    updateStatusUI(it)
                }
            }
        }
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.visibility.collectLatest { visibilityState ->
                    updateStateUI(visibilityState)
                }
            }
        }
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.countComplete.collectLatest {
                    updateCounter(it)
                }
            }
        }

        internetState = viewModel.status.value
    }
    private fun setUpUI() {
        views {
            addButt.setOnClickListener{
                val action = TaskFragDirection.actionManageTask(null)
                navController.navigate(action)
            }

            settingsButt.setOnClickListener {
                val action = TaskFragDirection.actionSetting()
                navController.navigate(action)
            }

            tasks.adapter = DealsAdapter(object : ItemListener {
                override fun onItemClick(id: String) {
                    val action = TaskFragDirection.actionManageTask(id = id)
                    navController.navigate(action)
                }

                override fun onCheckClick(todoItem: TodoItem) {
                    viewModel.updateItem(
                        todoItem.copy(
                            done = !todoItem.done
                        )
                    )
                }
            })

            val helper = SwipeHelper(object : SwipeInterface {
                override fun onDelete(todoItem: TodoItem) {
                    viewModel.deleteItem(todoItem)
                    showSnackbar(todoItem)
                }

                override fun onChangeDone(todoItem: TodoItem) {
                    viewModel.updateItem(
                        todoItem.copy(
                            done = !todoItem.done
                        )
                    )
                }
            }, context)
            helper.attachToRecyclerView(tasks)

            eye.setOnClickListener{
                viewModel.changeMode()
            }

            pullToRefresh.setOnRefreshListener {
                viewModel.loadNetworkList()
                pullToRefresh.isRefreshing = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showSnackbar(todoItem: TodoItem) {
        val snackbar = Snackbar.make(binding.tasks, "", Snackbar.LENGTH_INDEFINITE)
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE

        val customize = layoutInflater.inflate(R.layout.custom_snackbar, null)
        snackbar.view.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
        val snackBarLayout = snackbar.view as Snackbar.SnackbarLayout
        val timerText = customize.findViewById<TextView>(R.id.timer)
        val timerTitle = customize.findViewById<TextView>(R.id.title)
        timerTitle.text = "Отменить удаление задачи ${todoItem.text} ?"
        val cancel = customize.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            viewModel.addItem(todoItem)
            snackbar.dismiss()
        }
        snackBarLayout.addView(customize, 0)
        val timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished/1000+1).toString()
            }

            override fun onFinish() {
                snackbar.dismiss()
            }
        }
        timer.start()

        snackbar.show()
    }

    private suspend fun updateStateUI(visibilityState: Boolean) {
        viewModel.data.collect { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    if (visibilityState) {
                        adapter.submitList(uiState.data.sortedBy { it.dateCreation.time })
                    } else {
                        adapter.submitList(uiState.data.filter { !it.done }
                            .sortedBy { it.dateCreation.time })
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
                        AppCompatResources.getColorStateList(context, R.color.green)
                    if (internetState != status) {
                        Toast.makeText(context, "Connected! Merging data...", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.loadNetworkList()
                    }
                }

                else -> {
                    connection.imageTintList =
                        AppCompatResources.getColorStateList(context, R.color.red)

                    if (internetState != status) {
                        Toast.makeText(context, "Internet Lost!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        internetState = status
    }

    private fun updateCounter(count: Int) {
        views {
            titleTemplate.text = "Выполнено - $count"
        }
    }


    private fun <T : Any> views(block: FragmentBlankBinding.() -> T): T = binding.block()
}