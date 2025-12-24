package com.example.animesearchapp.data.remote

import com.example.animesearchapp.data.remote.dto.AnimeDetailsDto
import com.example.animesearchapp.data.remote.dto.AnimeSearchDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShikimoriApi {

    @GET("animes")
    suspend fun searchAnimes(
        @Query("search") query: String,
        @Query("limit") limit: Int = 20,
        @Query("order") order: String = "popularity"
    ): List<AnimeSearchDto>

    @GET("animes/{id}")
    suspend fun getAnimeDetails(
        @Path("id") id: Int
    ): AnimeDetailsDto
}