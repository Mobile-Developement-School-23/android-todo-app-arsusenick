package com.example.authorisation.recyclerview

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.authorisation.R
import com.example.authorisation.data.dataBase.Importance
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.databinding.LowPrerBinding


class ViewHolder(private val binding: LowPrerBinding): RecyclerView.ViewHolder(binding.root) {

    var todoItem: TodoItem? = null
    fun bind(item: TodoItem, onItemList: ItemListener){
        this.todoItem = item
        if (item.done) {
            binding.lowTask.text = item.textTask
            binding.lowTask.paintFlags = binding.lowTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.lowTask.setTextColor(itemView.context.getColor(R.color.labTertiary))

            binding.lowCheckBox.isChecked = true
            binding.lowCheckBox.buttonTintList = AppCompatResources.getColorStateList(
                itemView.context,
                R.color.green
            )

            binding.arrow.visibility = View.GONE
            binding.lowDataDone.visibility = View.GONE
    }   else{
            binding.lowTask.text = item.textTask
            binding.lowTask.paintFlags = binding.lowTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.lowTask.setTextColor(itemView.context.getColor(R.color.labPrimary))
            binding.lowCheckBox.isChecked = false

            if(item.deadLine != null){
                binding.lowDataDone.visibility = View.VISIBLE
                binding.lowDataDone.text = item.deadlineToString()
            } else {
                binding.lowDataDone.visibility = View.GONE
            }
            //TODO глянуть что там как работает, может можно сделать по другому
            when(item.importance) {
                Importance.URGENT -> {
                    binding.arrow.visibility = View.VISIBLE

                    binding.lowCheckBox.buttonTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.red)
                    binding.arrow.setImageResource(R.drawable.warning)
                }
                Importance.LOW -> {
                    binding.arrow.visibility = View.VISIBLE

                    binding.lowCheckBox.buttonTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.supSeparator)
                    binding.arrow.setImageResource(R.drawable.arrow)
                }
                Importance.REGULAR -> {
                    binding.arrow.visibility = View.INVISIBLE

                    binding.lowCheckBox.buttonTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.supSeparator)
                }
            }
        }

        binding.lowCheckBox.setOnClickListener{
            onItemList.onCheckClick(item)
        }

        itemView.setOnClickListener {
            onItemList.onItemClick(item.id)
        }
    }


    companion object {
        fun create(parent: ViewGroup) = LowPrerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::ViewHolder)
    }
}