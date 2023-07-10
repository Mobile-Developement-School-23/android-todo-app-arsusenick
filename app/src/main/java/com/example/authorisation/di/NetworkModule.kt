package com.example.authorisation.di

import android.content.Context
import com.example.authorisation.SharedPreferencesHelper
import com.example.authorisation.internetThings.NetworkSource
import com.example.authorisation.internetThings.RetrofitService
import com.example.authorisation.internetThings.internetConnection.NetworkConnectivityObserver

import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {


    @Provides
    @Singleton
    fun provideBaseURL(): String = "https://beta.mrdekk.ru/todobackend/"

    @Provides
    @Singleton
    fun providePhoneID(sharedPreferencesHelper: SharedPreferencesHelper): String =
        sharedPreferencesHelper.getPhoneID()


    @Provides
    @Singleton
    fun provideRetrofitService(sharedPreferencesHelper: SharedPreferencesHelper): RetrofitService =
        provideRetrofitClient(sharedPreferencesHelper).create(RetrofitService::class.java)

    @Provides
    @Singleton
    fun provideRetrofitClient(sharedPreferencesHelper: SharedPreferencesHelper): Retrofit {
        System.setProperty("http.keepAlive", "false")
        return Retrofit.Builder()
            .client(provideHttpClient(sharedPreferencesHelper))
            .baseUrl(provideBaseURL())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideHttpClient(sharedPreferencesHelper: SharedPreferencesHelper): OkHttpClient =
        OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", provideAuthString(sharedPreferencesHelper))
                .build()
            chain.proceed(newRequest)
        }.connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .addInterceptor(getInterceptor()).build()


    @Provides
    @Singleton
    fun provideAuthString(sharedPreferencesHelper: SharedPreferencesHelper): String {
        return if (sharedPreferencesHelper.getToken() == "unaffordable") {
            "Bearer unaffordable"
        } else {
            "OAuth ${sharedPreferencesHelper.getToken()}"
        }
    }

    @Provides
    fun getInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }


    @Provides
    @Singleton
    fun provideNetworkSource(
        sharedPreferencesHelper: SharedPreferencesHelper,
        retrofitService: RetrofitService
    ):
            NetworkSource = NetworkSource(sharedPreferencesHelper, retrofitService)

    @Provides
    @Singleton
    fun provideConnectivityObserver(context: Context): NetworkConnectivityObserver =
        NetworkConnectivityObserver(context)

}