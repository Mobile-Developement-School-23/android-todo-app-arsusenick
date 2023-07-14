package com.example.authorisation.di.component

import com.example.authorisation.di.FragmentScope
import com.example.authorisation.di.modules.YandexAuthModule
import com.example.authorisation.ui.LoginFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [YandexAuthModule::class])
interface LoginComponent {
    fun inject(fragment: LoginFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }
}