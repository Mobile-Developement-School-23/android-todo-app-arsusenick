package com.example.authorisation.di.modules

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import com.example.authorisation.internetThings.Constants
import com.example.authorisation.internetThings.MyWorkManager
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class WorkManagerModule {


    @Singleton
    @Provides
    fun provideConstraints(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    @Singleton
    @Provides
    fun provideWorkManager(
        constraints: Constraints
    ): PeriodicWorkRequest = PeriodicWorkRequest.Builder(
        MyWorkManager::class.java,
        Constants.HOURS_FOR_UPDATE,
        TimeUnit.HOURS
    )
        .setConstraints(constraints)
        .addTag("update_data")
        .build()
}