package com.example.authorisation.di

import android.content.Context
import androidx.room.Room
import com.example.authorisation.data.dataBase.TodoItemDatabase
import com.example.authorisation.data.dataBase.TodoListDao
import dagger.Provides
import javax.inject.Singleton

class DataBaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): TodoItemDatabase = Room
        .databaseBuilder(
            context,
            TodoItemDatabase::class.java,
            "todolist_database"
        ).build()

    @Provides
    @Singleton
    fun provideTaskDao(database: TodoItemDatabase): TodoListDao = database.todoItemDao
}