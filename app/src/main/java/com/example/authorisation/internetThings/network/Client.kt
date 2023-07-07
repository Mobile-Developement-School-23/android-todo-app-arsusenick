package com.example.authorisation.internetThings.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Client {
    lateinit var retrofitClient: Retrofit
    private const val baseURL: String = "https://beta.mrdekk.ru/todobackend/"
    var token: String = BaseUrl.token
        set(value) {
            field = value
            retrofitClient = Retrofit.Builder()
                .client(getHttpClient())
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    fun createClient(): Retrofit {
        System.setProperty("http.keepAlive", "false")
        retrofitClient = Retrofit.Builder()
            .client(getHttpClient())
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofitClient
    }


    private fun getHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", getAuthString())
                .build()
            chain.proceed(newRequest)
        }.connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .addInterceptor(getInterceptor()).build()
    }

    private fun getAuthString(): String {
        return if (token == "unaffordable") {
            "Bearer unaffordable"
        } else {
            "OAuth $token"
        }
    }

    private fun getInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }
}