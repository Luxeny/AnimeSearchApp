package com.example.animesearchapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeSearchDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("russian") val russian: String? = null,
    @SerialName("image") val image: ImageDto? = null,
    @SerialName("score") val score: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList()
)