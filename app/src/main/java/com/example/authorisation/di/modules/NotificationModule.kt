package com.example.authorisation.di.modules

import com.example.authorisation.internetThings.notifications.NotificationsScheduler
import com.example.authorisation.internetThings.notifications.NotificationsSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface NotificationModule {

    @Reusable
    @Binds
    fun bindNotificationModule(notificationsSchedulerImpl: NotificationsSchedulerImpl): NotificationsScheduler
}