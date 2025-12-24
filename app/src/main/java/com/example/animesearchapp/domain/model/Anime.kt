package com.example.animesearchapp.domain.model

data class Anime(
    val id: Int,
    val name: String,
    val russian: String?,
    val imagePreviewUrl: String,
    val imageOriginalUrl: String,
    val score: Double?,
    val status: String?,
    val episodes: Int?,
    val episodesAired: Int?,
    val description: String?,
    val airedOn: String?,
    val releasedOn: String?,
    val genres: List<Genre>
) {
    val displayTitle: String get() = russian?.takeIf { it.isNotBlank() } ?: name
}