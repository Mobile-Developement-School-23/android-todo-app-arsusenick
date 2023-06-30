package com.example.authorisation.internetThings.network

import com.example.authorisation.internetThings.RetrofitService

object BaseUrl {
    private const val baseURL:String = "https://beta.mrdekk.ru/todobackend/"
    var updated_by = "1"
    val retrofitService: RetrofitService get() = Client.getClient(baseURL).create(RetrofitService::class.java)
}