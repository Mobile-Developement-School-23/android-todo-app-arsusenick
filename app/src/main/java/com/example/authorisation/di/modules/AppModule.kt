package com.example.authorisation.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideScope() = CoroutineScope(SupervisorJob())
}