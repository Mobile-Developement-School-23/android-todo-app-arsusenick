package com.example.authorisation

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.example.authorisation.internetThings.network.BaseUrl
import java.util.UUID

class SharedPreferencesHelper(context: Context) {
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

        BaseUrl.phoneID = sharedPreferences.getString("UID", "-1").toString()
        BaseUrl.token = sharedPreferences.getString("token", "no_token").toString()

    }

    fun putRevision(revision: Int) {
        editor.putInt("REVISION", revision)
        editor.apply()
    }

    fun getToken(): String {
        return sharedPreferences.getString("token", "unaffordable")!!
    }

    fun putToken(token: String) {
        editor.putString("token", token)
        BaseUrl.token = token
        editor.apply()
    }

    fun getLastRevision(): Int = sharedPreferences.getInt("REVISION", 1)


}