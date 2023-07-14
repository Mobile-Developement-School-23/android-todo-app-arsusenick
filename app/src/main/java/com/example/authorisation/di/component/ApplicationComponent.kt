package com.example.authorisation.di.component

import android.content.Context
import com.example.authorisation.App
import com.example.authorisation.MainActivity
import com.example.authorisation.di.AppScope
import com.example.authorisation.di.modules.AppModule
import com.example.authorisation.di.modules.DataBaseModule
import com.example.authorisation.di.modules.NetworkModule
import com.example.authorisation.di.modules.RepositoryModule
import com.example.authorisation.di.modules.SharedPreferencesHelperModule
import com.example.authorisation.di.modules.WorkManagerModule
import com.example.authorisation.internetThings.MyWorkManager
import com.example.authorisation.internetThings.ViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@AppScope
@Singleton
@Component(
    dependencies = [],
    modules = [AppModule::class,
        DataBaseModule::class,
        SharedPreferencesHelperModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        WorkManagerModule::class]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(workManager: MyWorkManager)
    fun inject(app: App)
    fun viewModelsFactory(): ViewModelFactory
    fun loginFragmentComponentBuilder(): LoginComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}