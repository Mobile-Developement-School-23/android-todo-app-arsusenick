package com.example.authorisation.data.dataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {

    @Query("SELECT * FROM todolist")
    fun getAllFlow(): Flow<List<TodoItemEnt>>


    @Query("SELECT * FROM todolist")
    fun getAll(): List<TodoItemEnt>

    @Query("SELECT * FROM todolist WHERE id=:itemId")
    fun getItem(itemId: String): TodoItemEnt

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(todoItem: TodoItemEnt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addList(newItems: List<TodoItemEnt>)

    @Update
    suspend fun updateItem(toDoItemEntity: TodoItemEnt)
    @Delete
    suspend fun delete(entity: TodoItemEnt)

    @Query("DELETE FROM todoList")
    suspend fun deleteAll()

    @Query("UPDATE todolist SET done= :done, changedAt=:time WHERE id = :id")
    suspend fun updateDone(id: String, done: Boolean, time:Long)
}