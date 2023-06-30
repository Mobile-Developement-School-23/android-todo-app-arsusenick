package com.example.authorisation.data.dataBase

import com.google.gson.Gson
import java.sql.Date
import java.text.SimpleDateFormat

//дата класс задач
data class TodoItem(
    var id: String,
    var textTask: String,
    var importance: Importance,
    var deadLine: Date?,
    var done: Boolean,
    var dateCreation: Date,
    var dateChanged: Date?
) {

    constructor() : this(
        id = "-1", textTask = "", importance = Importance.REGULAR,
        deadLine = null, done = false, dateCreation = Date(1000), dateChanged = null
    )

    fun deadlineToString(): String? {
        if (deadLine != null) {
            val dateFormat = SimpleDateFormat("dd MM YYYY")
            return dateFormat.format(deadLine!!)
        }
        return null
    }

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

}

enum class Importance {
    LOW,
    REGULAR,
    URGENT;
}