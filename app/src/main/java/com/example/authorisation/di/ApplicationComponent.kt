package com.example.authorisation.di

import com.example.authorisation.data.dataBase.TodoItemDatabase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(dependencies = [], modules = [ApplicationComponent::class, TodoItemDatabase::class])
internal interface ApplicationComponent