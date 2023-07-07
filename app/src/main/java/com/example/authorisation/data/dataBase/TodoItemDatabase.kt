package com.example.authorisation.data.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TodoItemEnt::class], version = 3)
abstract class TodoItemDatabase : RoomDatabase() {
    abstract val todoItemDao: TodoItemDao

    companion object {
        fun create(context: Context) = Room.databaseBuilder(
            context,
            TodoItemDatabase::class.java,
            "todolist_database"
        ).fallbackToDestructiveMigration().build()
    }
}