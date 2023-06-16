package com.example.authorisation.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.authorisation.BlankFragment
import com.example.authorisation.BlankFragment2
import com.example.authorisation.R
import com.example.authorisation.recyclerview.data.TaskPreview
//Здесь мы определяем какие могут быть поля в ресайкл вью
// Поле с номрмальным приоритетом
class NormalTaskPreviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val task: TextView = itemView.findViewById(R.id.task)
    private val taskCheck: AppCompatCheckBox = itemView.findViewById(R.id.checkBox)
    private val dataField: TextView = itemView.findViewById(R.id.dataDone)
    private var flag: Boolean? = null
    private val about: ImageButton = itemView.findViewById(R.id.about_butt)
    private val bundle = Bundle()

    fun onBind(taskPreview: TaskPreview){
//Фикс бага связанный с свайпом вверх, вниз после чего чек бокс принимал другое значение(в других приоритетах идет копипаст этого фрагмента) честно говоря мне не нравится то что тут написано
        if (flag == null) {
            flag = taskPreview.taskDone
            taskCheck.isChecked = flag as Boolean
        }
        taskCheck.setOnClickListener{
            when(flag){
                true -> {
                    taskCheck.isChecked = false
                    flag = false
                }
                false -> {
                    taskCheck.isChecked = true
                    flag = true
                }

                else -> {
                    error("damn daniel")
                }
            }
        }

        task.text = taskPreview.task
        if (taskPreview.taskData != null){
            dataField.text = taskPreview.taskData
        }
        else dataField.visibility = View.GONE
        about.setOnClickListener {
            bundle.putString("id", taskPreview.id)
            bundle.putString("text", taskPreview.task)
            bundle.putString("date", taskPreview.taskData)
            bundle.putInt("priority", 0)
            itemView.findNavController().navigate(R.id.blankFragment2, bundle)
        }

    }
}
//низкий приоритет
class LowTaskPreviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val task: TextView = itemView.findViewById(R.id.low_task)
    private val taskCheck: AppCompatCheckBox = itemView.findViewById(R.id.low_checkBox)
    private val dataField: TextView = itemView.findViewById(R.id.low_dataDone)
    private var flag: Boolean? = null
    private val about: ImageButton = itemView.findViewById(R.id.about_butt)
    private val bundle = Bundle()

    fun onBind(taskPreview: TaskPreview){
        if (flag == null) {
            flag = taskPreview.taskDone
            taskCheck.isChecked = flag as Boolean
        }
        taskCheck.setOnClickListener{
            when(flag){
                true -> {
                    taskCheck.isChecked = false
                    flag = false
                }
                false -> {
                    taskCheck.isChecked = true
                    flag = true
                }

                else -> {
                    error("damn daniel")
                }
            }
        }
        task.text = taskPreview.task
//        taskCheck.isChecked = taskPreview.taskDone
        if (taskPreview.taskData != null){
            dataField.text = taskPreview.taskData
        }
        else dataField.visibility = View.GONE
        about.setOnClickListener {
            bundle.putString("id", taskPreview.id)
            bundle.putString("text", taskPreview.task)
            bundle.putString("date", taskPreview.taskData)
            bundle.putInt("priority", 1)
            itemView.findNavController().navigate(R.id.blankFragment2, bundle)
        }
    }
}
//Высокий приоритет
class HighTaskPreviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val task: TextView = itemView.findViewById(R.id.high_task)
    private val taskCheck: AppCompatCheckBox = itemView.findViewById(R.id.high_checkBox)
    private val dataField: TextView = itemView.findViewById(R.id.high_dataDone)
    private val about: ImageButton = itemView.findViewById(R.id.about_butt)
    private var flag: Boolean? = null
    private val bundle = Bundle()

    fun onBind(taskPreview: TaskPreview){
        if (flag == null) {
            flag = taskPreview.taskDone
            taskCheck.isChecked = flag as Boolean
        }
        taskCheck.setOnClickListener{
            when(flag){
                true -> {
                    taskCheck.isChecked = false
                    flag = false
                }
                false -> {
                    taskCheck.isChecked = true
                    flag = true
                }

                else -> {
                    error("damn daniel")
                }
            }
        }
        task.text = taskPreview.task
        if (taskPreview.taskData != null){
            dataField.text = taskPreview.taskData
        }
        else dataField.visibility = View.GONE
        about.setOnClickListener {
            bundle.putString("id", taskPreview.id)
            bundle.putString("text", taskPreview.task)
            bundle.putString("date", taskPreview.taskData)
            bundle.putInt("priority", 2)
            itemView.findNavController().navigate(R.id.blankFragment2, bundle)
        }
    }
}
//Поле для создание новой задачи(можно было и не делать, но почему бы и нет)
class NewTask(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val task: TextView = itemView.findViewById(R.id.task)
    private val taskCheck: AppCompatCheckBox = itemView.findViewById(R.id.checkBox)
    private val dataField: TextView = itemView.findViewById(R.id.dataDone)
    private val pic: ImageButton = itemView.findViewById(R.id.about_butt)
    private val layout: ConstraintLayout = itemView.findViewById(R.id.lay)


    @SuppressLint("ResourceAsColor")
    fun onBind(taskPreview: TaskPreview) {
        task.text = "Новая задача"
        task.setTextColor(R.color.labTertiary)
        taskCheck.visibility = View.INVISIBLE
        dataField.visibility = View.GONE
        pic.visibility = View.INVISIBLE
        layout.setOnClickListener {itemView ->
            itemView.findNavController().navigate(R.id.blankFragment2)}
    }

}
