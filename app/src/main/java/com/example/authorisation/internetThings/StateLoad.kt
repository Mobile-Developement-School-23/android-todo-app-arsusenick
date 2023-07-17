package com.example.authorisation.internetThings

sealed class StateLoad <out T> {
    object Initial : StateLoad<Nothing>()
    data class Result<T>(val data: T) : StateLoad<T>()
    data class Exception(val cause: Throwable): StateLoad<Nothing>()
}