package com.example.authorisation.internetThings.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.example.authorisation.App
import com.example.authorisation.R
import com.example.authorisation.data.dataBase.TodoItem
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var scheduler: NotificationsSchedulerImpl
    @Inject
    lateinit var coroutineScope: CoroutineScope
    private companion object {
        const val CHANNEL_ID = "deadlines"
        const val CHANNEL_NAME = "Deadline notification"
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as App).appComponent.inject(this)
        try {

            val gson = Gson()
            val item = gson.fromJson(intent.getStringExtra("item"), TodoItem::class.java)
            coroutineScope.launch(Dispatchers.IO) {

                val manager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                )

                val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_check_24dp)
                    .setContentTitle("Остался час!")
                    .setContentText(item.text)
                    .setAutoCancel(true)
                    .setContentIntent(deepLinkIntent(context, item.id))
                    .addAction(
                        NotificationCompat.Action(
                            R.drawable.ic_delay, "Отложить на день",
                            postponeIntent(context, item)
                        )
                    )
                    .build()
                scheduler.cancel(item.id)
                manager.notify(item.id.hashCode(), notification)
            }
        } catch (err: Exception) {
            Log.d("exception", err.stackTraceToString())
        }
    }

    private fun deepLinkIntent(context: Context, id: String): PendingIntent =
        NavDeepLinkBuilder(context)
            .setGraph(R.navigation.rg)
            .setDestination(R.id.blankFragment2, bundleOf("id" to id))
            .createPendingIntent()

    private fun postponeIntent(context: Context, item: TodoItem): PendingIntent =
        PendingIntent.getBroadcast(
            context, item.id.hashCode(),
            Intent(context, NotificationPostponeReceiver::class.java).apply {
                putExtra("item", item.toString())
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}