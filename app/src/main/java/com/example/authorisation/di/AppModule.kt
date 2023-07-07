package com.example.authorisation.di

import android.content.Context
import dagger.Provides
import javax.inject.Singleton

class AppModule (
    private val context: Context
) {
    @Provides
    @Singleton
    fun provideContext(): Context = context
}