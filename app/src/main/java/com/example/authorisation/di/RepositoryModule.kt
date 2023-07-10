package com.example.authorisation.di

import com.example.authorisation.data.rep.RepInterface
import com.example.authorisation.data.rep.TodoItemsRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindTodoRepository(todoItemsRepository: TodoItemsRepository): RepInterface
}