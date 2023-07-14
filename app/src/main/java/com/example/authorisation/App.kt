package com.example.authorisation

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.authorisation.di.component.ApplicationComponent
import com.example.authorisation.di.component.DaggerApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

class App : Application() {


    @Inject
    lateinit var myWorkRequest: PeriodicWorkRequest

    @Inject
    lateinit var sharedPreferencesHelper:SharedPreferencesHelper


    lateinit var appComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.factory().create(this)
        appComponent.inject(this)

        sharedPreferencesHelper.setMode(sharedPreferencesHelper.getMode())
        periodicUpdate()
    }

    private fun periodicUpdate() {
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "update_data",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            myWorkRequest
        )
    }


}