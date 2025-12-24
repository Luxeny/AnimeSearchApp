package com.example.animesearchapp.presentation.util

import com.example.animesearchapp.core.result.NetworkError

fun NetworkError.toUserMessage(): String = when (this) {
    NetworkError.NoInternet -> "Нет подключения к интернету"
    NetworkError.RateLimit -> "Слишком много запросов, попробуйте позже"
    NetworkError.ServiceUnavailable -> "Сервис временно недоступен"
    is NetworkError.HttpError -> "Ошибка сервера: ${code}"
    is NetworkError.Unknown -> "Неизвестная ошибка"
}
