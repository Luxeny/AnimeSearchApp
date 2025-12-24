package com.example.animesearchapp.core.result

sealed class NetworkError {
    data object NoInternet : NetworkError()
    data object RateLimit : NetworkError()
    data object ServiceUnavailable : NetworkError()
    data class HttpError(val code: Int, val message: String?) : NetworkError()
    data class Unknown(val message: String?) : NetworkError()
}