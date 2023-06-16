package com.example.authorisation

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
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.example.authorisation.spinner.CustomDropDownAdapter
import com.example.authorisation.spinner.Model
import com.example.authorisation.spinner.SpinnerRep
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//Фрагмент создания/редактирования задачи

class BlankFragment2 : Fragment(),DatePickerDialog.OnDateSetListener {
    private lateinit var spin: AppCompatSpinner
    private val spinRep = SpinnerRep()
    private var flag = 0
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat ("dd.MM.yyyy", Locale.UK)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spin = view.findViewById(R.id.spinner)
        val editText:EditText = view.findViewById(R.id.info_text)
        val turn: SwitchCompat = view.findViewById(R.id.turnDate)
        val date: TextView = view.findViewById(R.id.date)
        val cancelButt: ImageButton = view.findViewById(R.id.cancel)
        val deleteButt: ConstraintLayout = view.findViewById(R.id.deleteButt)
        val saveButt: TextView = view.findViewById(R.id.save)
        val bundle = Bundle()
        val modelList = arrayListOf<Model>()
        val dialog: DatePickerDialog = DatePickerDialog(requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

//Заполнение кастом спинера
        modelList.add(Model("Нет",null))
        modelList.add(Model("Низкий",R.drawable.arrow))
        modelList.add(Model("Высокий",R.drawable.warning))

        val customDropDownAdapter = CustomDropDownAdapter(requireContext(), modelList)
        spin.adapter = customDropDownAdapter

        editText.setText(arguments?.getString("text"))
        arguments?.getInt("priority")?.let { spin.setSelection(it) }
        if (arguments?.getString("date") != null){
            date.text = arguments?.getString("date")
            turn.isChecked = true
            flag = 1
        }

//кнопка назад
        cancelButt.setOnClickListener{
            findNavController().navigate(R.id.blankFragment)
        }
//Кнопка удаления(функционал не соответствует описанию изза тз)
        deleteButt.setOnClickListener{
            findNavController().navigate(R.id.blankFragment)
        }
//Кнопка сохранить(функционал не соответствует описанию изза тз)
        //TODO реализовать функционал сохранения в репозиторий
        saveButt.setOnClickListener{
            findNavController().navigate(R.id.blankFragment)
        }
//Обработка различных событий когда у нас открывается календарь с выбором дня (довольно странно, но это работает)
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
               1 -> {if(date.text == "Дата") {
                   turn.isChecked = false
                   flag = 0
               }
               }
               0 -> {
                   turn.isChecked = false
               }
           }

        }
        turn.setOnClickListener{
            when(flag) {
                0 -> {
                    flag = 1
                    turn.isChecked = true
                    dialog.show()
                }
                else -> {
                    flag = 0
                    turn.isActivated = false
                    date.text = "Дата"
                }
            }
        }

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