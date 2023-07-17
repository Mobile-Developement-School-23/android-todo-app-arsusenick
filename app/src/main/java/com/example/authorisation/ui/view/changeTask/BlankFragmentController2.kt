package com.example.authorisation.ui.view.changeTask

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.TextViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.authorisation.R
import com.example.authorisation.data.dataBase.Importance
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.databinding.FragmentBlank2Binding
import com.example.authorisation.internetThings.Constants
import com.example.authorisation.internetThings.network.UiState
import com.example.authorisation.spinner.CustomDropDownAdapter
import com.example.authorisation.spinner.Model
import com.example.authorisation.ui.stateHold.ManageTaskViewModel
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.Calendar
import java.util.UUID

class BlankFragmentController2(
    private val context: Context,
    private val navController: NavController,
    private val binding: FragmentBlank2Binding,
    private val lifecycleOwner: LifecycleOwner,
    private val model: ManageTaskViewModel,
    private val args: TaskFragArgs
) {
    private lateinit var timePickerDialog: DatePickerDialog
    private val modelList = arrayListOf<Model>()

    fun onCreate() {
        val id = args.id
        if (id != null) {
            model.getItem(id)
        } else {
            model.setItem()
        }
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.todoItem.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            updateViewsInfo(state.data)
                            setUpViews(state.data)
                            createSpinner(state.data)
                        }
                        else -> { /*wtf?*/ }
                    }
                }
            }
        }
    }

    private fun createSpinner(todoItem: TodoItem){
        modelList.add(Model("Нет",null))
        modelList.add(Model("Низкий", R.drawable.arrow))
        modelList.add(Model("Высокий", R.drawable.warning))
        val customDropDownAdapter = CustomDropDownAdapter(context, modelList)
        binding.spinner.adapter = customDropDownAdapter

        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                when (position){
                    0 -> {
                        todoItem.importance = Importance.REGULAR
                    }
                    1 -> {
                        todoItem.importance = Importance.LOW
                    }
                    2 -> {
                        todoItem.importance = Importance.URGENT
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun updateViewsInfo(todoItem: TodoItem){
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


        if (todoItem.id != "-1") {
            binding.delete.setTextColor(
                AppCompatResources.getColorStateList(
                    context,
                    R.color.red
                )
            )

            TextViewCompat.setCompoundDrawableTintList(
                binding.delete, AppCompatResources.getColorStateList(
                    context,
                    R.color.red
                )
            )
        }

    }

    private fun setUpViews(todoItem: TodoItem) {
        setUpDatePicker(todoItem)

        binding.turnDate.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                openDatePicker()
            } else {
                binding.date.visibility = View.INVISIBLE
                todoItem.deadline = null
            }
        }

        binding.delete.setOnClickListener {
            if (args.id != null) {
                model.deleteItem(todoItem)
                navController.popBackStack()
            }
        }
        binding.infoText.doAfterTextChanged {
            todoItem.text = binding.infoText.text.toString()
        }
        binding.cancel.setOnClickListener {
            YoYo.with(Techniques.BounceIn)
                .duration(Constants.ANIMATION_DURATION)
                .playOn(binding.cancel)
            navController.popBackStack()
        }

        binding.save.setOnClickListener {
            if (args.id == null) {
                saveNewTask(todoItem)
            } else {
                updateTask(todoItem)
            }
        }
    }
    private fun setUpDatePicker(todoItem: TodoItem) {
        val myCalendar = Calendar.getInstance()
        if (todoItem.deadline != null) {
            myCalendar.time = todoItem.deadline!!
        }
        timePickerDialog = DatePickerDialog(
            context,
            R.style.DatePickerStyle,
            { _, year, month, day ->
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

        timePickerDialog.setOnCancelListener {
            if (binding.date.visibility == View.INVISIBLE) {
                binding.turnDate.isChecked = false
            }
        }
    }

    private fun saveNewTask(todoItem: TodoItem) {
        todoItem.id = UUID.randomUUID().toString()
        todoItem.text = binding.infoText.text.toString()
        todoItem.dateCreation = Date(System.currentTimeMillis())
        if (todoItem.text.isEmpty()) {
            Toast.makeText(context, "Заполните что нужно сделать!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        model.addItem(todoItem)
        navController.popBackStack()

    }

    private fun updateTask(todoItem: TodoItem) {
        todoItem.text = binding.infoText.text.toString()
        todoItem.dateChanged = Date(System.currentTimeMillis())
        if (todoItem.text.isEmpty()) {
            Toast.makeText(context, "Заполните что нужно сделать!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        model.updateItem(todoItem)
        navController.popBackStack()
    }

    private fun openDatePicker() {
        binding.turnDate.isChecked = true
        timePickerDialog.show()
    }

}