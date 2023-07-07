package com.example.authorisation.ui

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.authorisation.R
import com.example.authorisation.data.dataBase.Importance
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.databinding.FragmentBlank2Binding
import com.example.authorisation.internetThings.internetConnection.ConnectivityObserver
import com.example.authorisation.model.MyViewModel
import com.example.authorisation.model.factory
import com.example.authorisation.spinner.CustomDropDownAdapter
import com.example.authorisation.spinner.Model
import com.example.authorisation.spinner.SpinnerRep
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar

import java.util.Locale
import java.util.UUID

//Фрагмент создания/редактирования задачи

class BlankFragment2 : Fragment(),DatePickerDialog.OnDateSetListener {

    private var todoItem = TodoItem()
    private lateinit var binding: FragmentBlank2Binding
    private lateinit var dialog: DatePickerDialog

    private var importent = 0
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat ("dd.MM.yyyy", Locale.UK)
    private val modelList = arrayListOf<Model>()

    private val args: TaskFragArgs by navArgs()

    private val model: MyViewModel by activityViewModels{factory()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBlank2Binding.inflate(layoutInflater)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBlank2Binding.inflate(LayoutInflater.from(context)).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = args.id
        if (id != null && savedInstanceState == null) {
            model.getItem(id)
            lifecycleScope.launch {
                model.item.collect {
                    if(todoItem.id == "-1") {
                        todoItem = it
                        updateViewsInfo()
                        setUpViews()
                    }
                }
            }
        } else if (savedInstanceState == null) {
            setUpViews()
        }

        if (savedInstanceState != null) {
            val gson = Gson()
            todoItem = gson.fromJson(savedInstanceState.getString("todoItem"), TodoItem::class.java)
            updateViewsInfo()
            setUpViews()
        }

        createSpinner()
    }

    private fun createSpinner(){
        modelList.add(Model("Нет",null))
        modelList.add(Model("Низкий", R.drawable.arrow))
        modelList.add(Model("Высокий", R.drawable.warning))
        val customDropDownAdapter = CustomDropDownAdapter(requireContext(), modelList)
        binding.spinner.adapter = customDropDownAdapter

        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                when (position){
                    0 -> {
                        todoItem.importance = Importance.REGULAR
                        importent = 0
                    }
                    1 -> {
                        todoItem.importance = Importance.LOW
                        importent = 1
                    }
                    2 -> {
                        todoItem.importance = Importance.URGENT
                        importent = 2
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
    private fun updateViewsInfo(){
        binding.infoText.setText(todoItem.text)

        when(todoItem.importance){
            Importance.LOW -> {
                binding.spinner.setSelection(1)
            }
            Importance.REGULAR -> {
                binding.spinner.setSelection(0)
            }
            Importance.URGENT -> {
                binding.spinner.setSelection(2)
            }
        }

        if (todoItem.deadline != null) {
            binding.date.visibility = View.VISIBLE
            binding.date.text = todoItem.deadlineToString()
            binding.turnDate.isChecked = true
        }

    }

    private fun setUpViews(){
        val myCalendar = Calendar.getInstance()
        if (todoItem.deadline != null) {
            myCalendar.time = todoItem.deadline!!
        }
        dialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerStyle,
            { view, year, month, day ->
                binding.date.visibility = View.VISIBLE
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                todoItem.deadline = Date(myCalendar.timeInMillis)
                binding.date.text = todoItem.deadlineToString()
            },
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.setOnCancelListener {
            if (binding.date.visibility == View.INVISIBLE) {
                binding.turnDate.isChecked = false
            }
        }

        binding.turnDate.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                openDatePicker()
            } else {
                binding.date.visibility = View.INVISIBLE
                todoItem.deadline = null
            }
        }


        binding.deleteButt.setOnClickListener {
            if (args.id != null) {

                if (model.status.value == ConnectivityObserver.Status.Available) {
                    model.deleteNetworkItem(todoItem.id)
                } else {
                    Toast.makeText(
                        context,
                        "No network, will delete later. Continue offline.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                model.deleteItem(todoItem)
                model.nullItem()
                findNavController().popBackStack()

            }
        }

        binding.cancel.setOnClickListener {
            YoYo.with(Techniques.BounceIn)
                .duration(200)
                .playOn(binding.cancel)

            model.nullItem()
            findNavController().popBackStack()
        }

        binding.save.setOnClickListener {
            if (args.id == null) {
                saveNewTask()
            } else {
                updateTask()
            }

        }
    }

    private fun saveNewTask() {
        todoItem.id = UUID.randomUUID().toString()
        todoItem.text = binding.infoText.text.toString()
        todoItem.dateCreation = Date(System.currentTimeMillis())
        if (todoItem.text.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните что нужно сделать!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (model.status.value == ConnectivityObserver.Status.Available) {
            model.updateNetworkItem(todoItem)
        } else {
            Toast.makeText(
                context,
                "No network, will upload later. Continue offline.",
                Toast.LENGTH_SHORT
            ).show()
        }
        model.addItem(todoItem)
        model.nullItem()
        findNavController().popBackStack()
    }

    private fun updateTask() {
        todoItem.text = binding.infoText.text.toString()
        todoItem.dateChanged = Date(System.currentTimeMillis())
        if (todoItem.text.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните что нужно сделать!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (model.status.value == ConnectivityObserver.Status.Available) {
            model.uploadNetworkItem(todoItem)
        } else {
            Toast.makeText(
                context,
                "No network, will update later. Continue offline.",
                Toast.LENGTH_SHORT
            ).show()
        }
        model.updateItem(todoItem)
        model.nullItem()
        findNavController().popBackStack()
    }

    private fun saveStates() {
        todoItem.text = binding.infoText.text.toString()
        when (importent) {
            2 -> {
                todoItem.importance = Importance.URGENT
                println("2")
            }

            0 -> {
                todoItem.importance = Importance.REGULAR
                println("0")
            }

            1 -> {
                todoItem.importance = Importance.LOW
                println("1")
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveStates()
        outState.putString("todoItem", todoItem.toString())
    }

    private fun openDatePicker() {
        binding.turnDate.isChecked = true
        dialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        println("das")
        setDate(calendar.timeInMillis)
    }
    private fun setDate(timestamp: Long){
        view?.findViewById<TextView>(R.id.date)?.text = formatter.format(timestamp)
    }
}