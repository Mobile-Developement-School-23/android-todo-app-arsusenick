package com.example.authorisation.internetThings

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class InternetConnection(
    val context:Context
) {
    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val n = cm.activeNetwork
        if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
        return false
    }
}