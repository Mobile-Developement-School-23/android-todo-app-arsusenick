package com.example.authorisation

import android.app.Application
import android.content.Context
import com.example.authorisation.internetThings.InternetConnection
import com.example.authorisation.internetThings.ServiceLocator
import com.example.authorisation.internetThings.locale
import com.example.authorisation.data.dataBase.TodoItemDatabase
import com.example.authorisation.data.rep.TodoItemsRepository

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        ServiceLocator.register<Context>(this)
        ServiceLocator.register(SharedPreferencesHelper(applicationContext))
        ServiceLocator.register(TodoItemDatabase.create(locale()))
        ServiceLocator.register(TodoItemsRepository(locale(), locale()))
        ServiceLocator.register(InternetConnection(applicationContext))
    }
}