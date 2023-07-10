package com.example.authorisation.internetThings.network

import com.example.authorisation.internetThings.RetrofitService

object BaseUrl {
    var token = "no_token"
        set(value) {
            field = value
            RetrofitClient.token = value
        }
    var phoneID = "id"
    val retrofitService: RetrofitService =
        RetrofitClient.createClient().create(RetrofitService::class.java)
}