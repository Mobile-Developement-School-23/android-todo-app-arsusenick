package com.example.authorisation

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatDelegate
import com.example.authorisation.internetThings.Constants
import com.example.authorisation.internetThings.Mode
import com.example.authorisation.internetThings.network.BaseUrl
import java.util.UUID
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(
    context: Context
) {

    private val sharedPreferences: SharedPreferences
    private val editor: Editor

    init {
        sharedPreferences = context.getSharedPreferences("states", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        if (!sharedPreferences.contains("UID")) {
            editor.putString("UID", UUID.randomUUID().toString())
        }

        if (!sharedPreferences.contains("token")) {
            putToken("no_token")
        }

        if(!sharedPreferences.contains("mode")){
            editor.putString("mode", "system")
        }

        if(!sharedPreferences.contains("notifications")){
            editor.putString("notifications", "hey")
        }

        if(!sharedPreferences.contains("notification_permission")){
            editor.putString("notification_permission", "none")
        }
        editor.apply()

        Constants.phoneId = getPhoneID()
    }

    fun putRevision(revision: Int) {
        editor.putInt("REVISION", revision)
        editor.apply()
    }

    fun getToken(): String {
        return sharedPreferences.getString("token", "unaffordable")!!
    }

    fun getTokenForResponse():String{
        return if (getToken() == "unaffordable") {
            "Bearer unaffordable"
        } else {
            "OAuth ${getToken()}"
        }
    }

    fun getMode(): Mode {
        return when(sharedPreferences.getString("mode", "system")){
            "dark" -> Mode.NIGHT
            "light" -> Mode.LIGHT
            else -> Mode.SYSTEM
        }
    }

    fun setMode(mode:Mode) {
        when(mode){
            Mode.NIGHT -> {
                editor.putString("mode", "dark")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Mode.LIGHT -> {
                editor.putString("mode", "light")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Mode.SYSTEM -> {
                editor.putString("mode", "system")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
        editor.apply()
    }

    fun addNotification(id:String):String{
        editor.putString("notifications", getNotificationsId()+" $id")
        editor.apply()
        return sharedPreferences.getString("notifications", "").toString()
    }
    fun removeNotification(id:String){
        val s = getNotificationsId()
        val arr = ArrayList(s.split(" "))
        if(arr.contains(id)){
            arr.remove(id)
        }
        val res = arr.fold("") { previous, next -> "$previous $next" }
        editor.putString("notifications", res)
        editor.apply()
    }
    fun getNotificationsId():String{
        return sharedPreferences.getString("notifications", "").toString()
    }

    fun putNotificationPermission(permitted:Boolean) {
        when(permitted){
            true -> editor.putString("notifications_permission", "true")
            false -> editor.putString("notifications_permission", "false")
        }
        editor.apply()
    }
    fun getNotificationPermission() : String {
        return sharedPreferences.getString("notifications_permission", "none").toString()
    }
    private fun getPhoneID():String = sharedPreferences.getString("UID", "uid").toString()
    fun putToken(token: String) {
        editor.putString("token", token)
        editor.apply()
    }

    fun getLastRevision(): Int = sharedPreferences.getInt("REVISION", 1)


}