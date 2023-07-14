package com.example.authorisation.di.component

import android.content.Context
import com.example.authorisation.App
import com.example.authorisation.MainActivity
import com.example.authorisation.di.AppScope
import com.example.authorisation.di.modules.AppModule
import com.example.authorisation.di.modules.DataBaseModule
import com.example.authorisation.di.modules.NetworkModule
import com.example.authorisation.di.modules.NotificationModule
import com.example.authorisation.di.modules.RepositoryModule
import com.example.authorisation.di.modules.SharedPreferencesHelperModule
import com.example.authorisation.di.modules.WorkManagerModule
import com.example.authorisation.internetThings.MyWorkManager
import com.example.authorisation.internetThings.ViewModelFactory
import com.example.authorisation.internetThings.notifications.NotificationPostponeReceiver
import com.example.authorisation.internetThings.notifications.NotificationsReceiver
import com.example.authorisation.ui.view.settings.BottomSheetFragment
import com.example.authorisation.ui.view.settings.SettingsFragment
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
        WorkManagerModule::class,
        NotificationModule::class,
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(workManager: MyWorkManager)
    fun inject(app:App)
    fun inject(notificationPostponeReceiver: NotificationPostponeReceiver)
    fun inject(notificationsReceiver: NotificationsReceiver)
    fun inject(fragment: BottomSheetFragment)
    fun inject(fragment: SettingsFragment)
    fun viewModelsFactory(): ViewModelFactory
    fun loginFragmentComponentBuilder(): LoginComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}