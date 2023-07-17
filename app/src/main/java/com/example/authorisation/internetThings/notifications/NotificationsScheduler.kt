package com.example.authorisation.internetThings.notifications

import com.example.authorisation.data.dataBase.TodoItem


interface NotificationsScheduler {
    fun schedule(item: TodoItem)
    fun cancel(id:String)
    fun cancelAll()
}