package com.example.authorisation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.authorisation.recyclerview.TodoAdapter
import com.example.authorisation.recyclerview.data.TaskPreview
import com.example.authorisation.recyclerview.data.TodoItemsRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

//фрагмент основного экрана

class BlankFragment : Fragment() {
    private lateinit var list: MutableList<TaskPreview>
    private lateinit var taskRecyclerView: RecyclerView
    private val tasksPreviewRepository = TodoItemsRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskRecyclerView = view.findViewById(R.id.tasks)

//описание тулбара и подключение его из активити

        val toolbar: Toolbar = view.findViewById(R.id.toolBar)
        val eyeButton: ToggleButton = view.findViewById(R.id.eye)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val button: FloatingActionButton = view.findViewById(R.id.add_butt)
        val taskAdapter = TodoAdapter()
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

//тут мы подгружаем из условного репозитория данные и заполняем список
        list = tasksPreviewRepository.getTasks(context) as MutableList<TaskPreview>
        taskRecyclerView.adapter = taskAdapter
        taskRecyclerView.layoutManager = layoutManager
        taskAdapter.tasks = list

//переход на другой фрагмент
        button.setOnClickListener{
            findNavController().navigate(R.id.blankFragment2)
        }

//пул ту рефреш, в данной реализации абсолютно бесполезная штука, когда подключимся к бд, будем использовать
        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.pull_to_refresh)
        pullToRefresh.setOnRefreshListener {
            taskAdapter.tasks = tasksPreviewRepository.getTasks(context)
            pullToRefresh.isRefreshing = false
        }
    }

}