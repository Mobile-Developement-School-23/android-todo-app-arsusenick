package com.example.authorisation.data.dataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "todoList")
data class TodoItemEnt(
    @PrimaryKey @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "importance") var importance: Importance,
    @ColumnInfo(name = "deadline") var deadline: Long?,
    @ColumnInfo(name = "done") var done: Boolean,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "changedAt") var changedAt: Long?
) {
    fun toItem(): TodoItem = TodoItem(
        id,
        description,
        importance,
        deadline?.let { Date(it) },
        done,
        Date(createdAt),
        changedAt?.let { Date(it) }
    )
    companion object {
        fun fromItem(toDoItem: TodoItem): TodoItemEnt {
            return TodoItemEnt(
                id = toDoItem.id,
                description = toDoItem.text,
                importance = toDoItem.importance,
                deadline = toDoItem.deadline?.time,
                done = toDoItem.done,
                createdAt = toDoItem.dateCreation.time,
                changedAt = toDoItem.dateChanged?.time
            )
        }
    }
}