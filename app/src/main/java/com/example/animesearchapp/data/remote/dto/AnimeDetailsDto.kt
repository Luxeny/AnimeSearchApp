package com.example.animesearchapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeDetailsDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("russian") val russian: String? = null,
    @SerialName("image") val image: ImageDto? = null,
    @SerialName("score") val score: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("episodes") val episodes: Int? = null,
    @SerialName("episodes_aired") val episodesAired: Int? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("aired_on") val airedOn: String? = null,
    @SerialName("released_on") val releasedOn: String? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList()
)