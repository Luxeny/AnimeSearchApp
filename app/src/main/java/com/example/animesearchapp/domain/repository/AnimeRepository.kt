package com.example.animesearchapp.domain.repository

import com.example.animesearchapp.core.result.AppResult
import com.example.animesearchapp.domain.model.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun observeSearchResults(query: String): Flow<List<Anime>>
    fun observeRecentResults(limit: Int): Flow<List<Anime>>

    /**
     * cache-then-network (TTL-aware). Emits cached first (if any), then refreshes (unless TTL ok or offline).
     * Returns network attempt result when performed, or null when skipped due to TTL and !force.
     */
    suspend fun refreshSearch(query: String, force: Boolean): AppResult<Unit>?

    fun observeAnimeDetails(animeId: Int): Flow<Anime?>
    suspend fun refreshAnimeDetails(animeId: Int, force: Boolean): AppResult<Unit>?
}