package com.example.authorisation.internetThings

sealed class StateLoad <out T> {
    data class Error<T>(val error: String) : StateLoad<T>()
    data class Loading<out T>(val isLoading: Boolean) : StateLoad<T>()
    data class Success<out T>(val data: T) : StateLoad<T>()
}