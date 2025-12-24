package com.example.animesearchapp.core.util

import com.example.animesearchapp.core.result.AppResult
import com.example.animesearchapp.core.result.NetworkError
import retrofit2.HttpException
import java.io.IOException

suspend inline fun <T> safeApiCall(crossinline block: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: IOException) {
        AppResult.Error(NetworkError.NoInternet)
    } catch (e: HttpException) {
        val code = e.code()
        when {
            code == 429 -> AppResult.Error(NetworkError.RateLimit)
            code in 500..599 -> AppResult.Error(NetworkError.ServiceUnavailable)
            else -> AppResult.Error(NetworkError.HttpError(code, e.message()))
        }
    } catch (t: Throwable) {
        AppResult.Error(NetworkError.Unknown(t.message))
    }
}