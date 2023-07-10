package com.example.authorisation

import android.app.Application
import android.content.Context
import com.example.authorisation.internetThings.ServiceLocator
import com.example.authorisation.internetThings.locale
import com.example.authorisation.data.dataBase.TodoItemDatabase
import com.example.authorisation.data.rep.TodoItemsRepository
import com.example.authorisation.di.AppModule
import com.example.authorisation.di.ApplicationComponent
import com.example.authorisation.di.DaggerApplicationComponent
import com.example.authorisation.internetThings.NetworkSource

import com.example.authorisation.internetThings.internetConnection.NetworkConnectivityObserver

class App: Application() {

    lateinit var appComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .build()

    }
}