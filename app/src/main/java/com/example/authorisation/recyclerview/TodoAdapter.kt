package com.example.authorisation.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.authorisation.R
import com.example.authorisation.recyclerview.data.Task
import com.example.authorisation.recyclerview.data.TaskPreview
import com.example.authorisation.recyclerview.domain.CommonCallbackImpl
//Самая важная часть кода, адаптер
class TodoAdapter: RecyclerView.Adapter<ViewHolder>() {
//подключения дифутила чтобы обновление было умным и не таким ресурсозатратным
    var tasks = listOf<TaskPreview>()
        set(value) {
            val callback = CommonCallbackImpl(
                oldItems = field,
                newItems = value,
                { oldItem: Task, newItem -> oldItem.id == newItem.id })
            field = value
            val diffResult = DiffUtil.calculateDiff(callback)
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType){
            NORMAL_TASK_PREVIEW_TYPE -> NormalTaskPreviewViewHolder(
                layoutInflater.inflate(
                    R.layout.normal_prer,
                    parent,
                    false
                )
            )

            LOW_TASK_PREVIEW_TYPE -> LowTaskPreviewViewHolder(
                layoutInflater.inflate(
                    R.layout.low_prer,
                    parent,
                    false
                )
            )

            HIGH_TASK_PREVIEW_TYPE -> HighTaskPreviewViewHolder(
                layoutInflater.inflate(
                    R.layout.high_prer,
                    parent,
                    false
                )
            )

            NEW_TASK -> NewTask(
                layoutInflater.inflate(
                    R.layout.normal_prer,
                    parent,
                    false
                )
            )
            else -> error("wrong type exception")
        }
    }

    override fun getItemCount() = tasks.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when(holder){
                is NormalTaskPreviewViewHolder -> holder.onBind(tasks[position] as TaskPreview)
                is LowTaskPreviewViewHolder -> holder.onBind(tasks[position] as TaskPreview)
                is HighTaskPreviewViewHolder -> holder.onBind(tasks[position] as TaskPreview)
                is NewTask -> holder.onBind(tasks[position] as TaskPreview)
            }
    }
//Получение состояний
    override fun getItemViewType(position: Int): Int {
        return when(tasks[position].taskState) {
            0 -> NORMAL_TASK_PREVIEW_TYPE
            1 -> LOW_TASK_PREVIEW_TYPE
            2 -> HIGH_TASK_PREVIEW_TYPE
            3 -> NEW_TASK
            else -> error("wrong type exception")
        }
    }
//Описание состояний
    companion object {
        private const val NORMAL_TASK_PREVIEW_TYPE = 0
        private const val LOW_TASK_PREVIEW_TYPE = 1
        private const val HIGH_TASK_PREVIEW_TYPE = 2
        private const val NEW_TASK = 3
    }
}