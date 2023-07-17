package com.example.authorisation.data.dataBase

import com.example.authorisation.internetThings.Constants.DEFAULT_DATE
import com.google.gson.Gson
import java.sql.Date
import java.text.SimpleDateFormat

//дата класс задач
data class TodoItem(
    var id: String,
    var text: String,
    var importance: Importance,
    var deadline: Date?,
    var done: Boolean,
    var dateCreation: Date,
    var dateChanged: Date?
) {

    constructor() : this(
        id = "-1", text = "", importance = Importance.REGULAR,
        deadline = null, done = false, dateCreation = Date(DEFAULT_DATE), dateChanged = null
    )

    fun deadlineToString(): String? {
        if (deadline != null) {
            val dateFormat = SimpleDateFormat("dd MMMM YYYY")
            return dateFormat.format(deadline!!)
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
