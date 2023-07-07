package com.example.authorisation.internetThings.network

import retrofit2.Response

sealed class NetworkAccess<out T> {
    data class Success<out T>(val data: T): NetworkAccess<T>()
    data class Error<T>(val response: Response<T>): NetworkAccess<T>()
}

fun <T> Response<T>.parseResponse(): NetworkAccess<T> {
    return if (this.isSuccessful && this.body() != null) {
        val responseBody = this.body()
        NetworkAccess.Success(responseBody!!)
    } else {
        NetworkAccess.Error(this)
    }
}