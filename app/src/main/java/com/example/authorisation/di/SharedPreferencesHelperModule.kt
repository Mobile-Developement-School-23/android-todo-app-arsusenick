package com.example.authorisation.di

import android.content.Context
import com.example.authorisation.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SharedPreferencesHelperModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferencesHelper =
        SharedPreferencesHelper(context)
}