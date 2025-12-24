package com.example.animesearchapp.core

object AppConfig {
    const val BASE_URL = "https://shikimori.one/api/"
    const val SHIKIMORI_HOST = "https://shikimori.one"

    // TTL 24h for both search-query cache and details cache
    const val CACHE_TTL_MS: Long = 24L * 60L * 60L * 1000L

    const val USER_AGENT = "AnimeSearchApp (Android; Kotlin; Compose)"
}