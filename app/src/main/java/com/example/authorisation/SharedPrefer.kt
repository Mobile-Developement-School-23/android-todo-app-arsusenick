package com.example.authorisation

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
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
            editor.apply()
        }

        if (!sharedPreferences.contains("token")) {
            putToken("no_token")
        }
    }

    fun putRevision(revision: Int) {
        editor.putInt("REVISION", revision)
        editor.apply()
    }

    fun getToken(): String {
        return sharedPreferences.getString("token", "unaffordable")!!
    }

    fun getPhoneID():String = sharedPreferences.getString("UID", "uid").toString()
    fun putToken(token: String) {
        editor.putString("token", token)
        editor.apply()
    }

    fun getLastRevision(): Int = sharedPreferences.getInt("REVISION", 1)


}