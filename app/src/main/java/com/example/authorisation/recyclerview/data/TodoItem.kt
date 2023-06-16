package com.example.authorisation.recyclerview.data

import java.util.Date
//дата класс задач
sealed class Task(
    val id: String
)

data class TaskPreview(
    val taskId: String,
    val taskState: Int,
    val task: String,
    val taskData: String?,
    val taskDone: Boolean,
    val dataCreate: String,
    val dateChange: String?
): Task(id = taskId)