package com.example.authorisation.internetThings.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.authorisation.App
import com.example.authorisation.data.dataBase.TodoItem
import com.example.authorisation.data.rep.RepInterface
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date
import javax.inject.Inject

class NotificationPostponeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: RepInterface
    @Inject
    lateinit var coroutineScope: CoroutineScope
    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as App).appComponent.inject(this)

        val gson = Gson()
        val item = gson.fromJson(intent.getStringExtra("item"), TodoItem::class.java)
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(item.id.hashCode())
        try {
            coroutineScope.launch(Dispatchers.IO) {
                if(item.deadline != null){
                    repository.changeItem(item.copy(deadline = Date(item.deadline!!.time+86400000)))
                }
            }
        }catch (err:Exception){
            Log.d("1", err.message.toString())
        }
    }
}