package com.example.authorisation.di

import com.example.authorisation.MainActivity
import com.example.authorisation.model.ViewModelFactory
import com.example.authorisation.ui.BlankFragment
import com.example.authorisation.ui.BlankFragment2
import com.example.authorisation.ui.LoginFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(dependencies = [], modules = [AppModule::class, DataBaseModule::class, SharedPreferencesHelperModule::class, NetworkModule::class])
interface ApplicationComponent {

    fun viewModelsFactory(): ViewModelFactory
    fun inject(activity: MainActivity)
    fun inject(fragment: LoginFragment)
    fun inject(fragment:BlankFragment)
    fun inject(fragment:BlankFragment2)

}