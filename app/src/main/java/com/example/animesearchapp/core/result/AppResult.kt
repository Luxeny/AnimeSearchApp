package com.example.animesearchapp.core.result

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val error: NetworkError) : AppResult<Nothing>()
}