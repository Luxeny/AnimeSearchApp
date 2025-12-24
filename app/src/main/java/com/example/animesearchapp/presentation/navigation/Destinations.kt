package com.example.animesearchapp.presentation.navigation

object Destinations {
    const val SEARCH = "search"
    const val DETAILS = "details"
    const val ARG_ANIME_ID = "animeId"

    fun detailsRoute(animeId: Int): String = "$DETAILS/$animeId"
    const val detailsPattern: String = "$DETAILS/{$ARG_ANIME_ID}"
}
