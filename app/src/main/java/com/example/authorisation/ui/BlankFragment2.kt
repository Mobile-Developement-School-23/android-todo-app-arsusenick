package com.example.authorisation.ui

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


    private lateinit var spin: AppCompatSpinner
    private val spinRep = SpinnerRep()
    private var flag = 0
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
    ): View {

        modelList.add(Model("Нет",null))
        modelList.add(Model("Низкий", R.drawable.arrow))
        modelList.add(Model("Высокий", R.drawable.warning))
        val customDropDownAdapter = CustomDropDownAdapter(requireContext(), modelList)
        binding.spinner.adapter = customDropDownAdapter
        val id = args.id
        if (id != null && savedInstanceState == null) {
            model.getItem(id)
            lifecycleScope.launch {
                model.task.collect {
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
        return binding.root
    }

    private fun updateViewsInfo(){
        binding.infoText.setText(todoItem.textTask)

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

        if (todoItem.deadLine != null) {
            binding.date.visibility = View.VISIBLE
            binding.date.text = todoItem.deadlineToString()
            binding.turnDate.isChecked = true
        }

//        if (todoItem.id != "-1") {
//            binding.delete.setTextColor(
//                AppCompatResources.getColorStateList(
//                    requireContext(),
//                    R.color.red
//                )
//            )

    }

    private fun setUpViews(){
        val myCalendar = Calendar.getInstance()
        if (todoItem.deadLine != null) {
            myCalendar.time = todoItem.deadLine!!
        }
        val dialog: DatePickerDialog = DatePickerDialog(requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

        dialog.setButton(
            DialogInterface.BUTTON_NEGATIVE, "Cancel")
        { _, which ->
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                flag = 0
                println("222")
            }
        }
        dialog.setOnDismissListener {
            when(flag){
                1 -> {if(binding.date.text == "Дата") {
                    binding.turnDate.isChecked = false
                    flag = 0
                }
                }
                0 -> {
                    binding.turnDate.isChecked = false
                }
            }

        }
        binding.turnDate.setOnClickListener{
            when(flag) {
                0 -> {
                    flag = 1
                    binding.turnDate.isChecked = true
                    dialog.show()
                }
                else -> {
                    flag = 0
                    binding.turnDate.isActivated = false
                    binding.date.text = "Дата"
                }
            }
        }

        binding.deleteButt.setOnClickListener {
            if (args.id != null){
                model.delItem(todoItem)
                findNavController().popBackStack()
            }
        }

        binding.cancel.setOnClickListener {
            YoYo.with(Techniques.BounceIn)
                .duration(200)
                .playOn(binding.cancel)

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

    private fun saveNewTask(){
        todoItem.id = UUID.randomUUID().toString()
        todoItem.textTask = binding.infoText.text.toString()
        todoItem.dateCreation = Date(System.currentTimeMillis())

        if (todoItem.textTask.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните что нужно сделать!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        model.addItem(todoItem)
        findNavController().popBackStack()
    }

    private fun updateTask() {
        todoItem.textTask = binding.infoText.text.toString()
        todoItem.dateChanged = Date(System.currentTimeMillis())
        if (todoItem.textTask.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните что нужно сделать!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        model.upItem(todoItem)
        findNavController().popBackStack()
    }

    private fun saveStates() {
        todoItem.textTask = binding.infoText.text.toString()
        when (binding.spinner.selectedItemPosition) {
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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        println("das")
        setDate(calendar.timeInMillis)
    }
    private fun setDate(timestamp: Long){
        view?.findViewById<TextView>(R.id.date)?.text = formatter.format(timestamp)
    }
}