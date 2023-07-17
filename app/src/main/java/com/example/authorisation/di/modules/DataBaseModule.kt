package com.example.authorisation.di.modules

import android.content.Context
import androidx.room.Room
import com.example.authorisation.data.dataBase.TodoItemDatabase
import com.example.authorisation.data.dataBase.TodoListDao
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

@Module
class DataBaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context) = Room.databaseBuilder(
        context,
        TodoItemDatabase::class.java,
        "todolist_database"
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Reusable
    fun provideTaskDao(database: TodoItemDatabase): TodoListDao = database.todoItemDao
}