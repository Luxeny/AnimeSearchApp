package com.example.animesearchapp.data.mapper

import com.example.animesearchapp.core.AppConfig
import com.example.animesearchapp.data.local.entity.AnimeEntity
import com.example.animesearchapp.data.local.entity.relations.AnimeWithGenres
import com.example.animesearchapp.data.remote.dto.AnimeDetailsDto
import com.example.animesearchapp.data.remote.dto.AnimeSearchDto
import com.example.animesearchapp.domain.model.Anime

private fun String?.toScoreDoubleOrNull(): Double? = this?.toDoubleOrNull()

private fun fullImageUrl(path: String?): String {
    if (path.isNullOrBlank()) return ""
    return if (path.startsWith("http")) path else AppConfig.SHIKIMORI_HOST + path
}

fun AnimeSearchDto.toEntity(now: Long): AnimeEntity = AnimeEntity(
    id = id,
    name = name,
    russian = russian,
    imagePreviewUrl = fullImageUrl(image?.preview),
    imageOriginalUrl = fullImageUrl(image?.original),
    score = score.toScoreDoubleOrNull(),
    status = status,
    episodes = null,
    episodesAired = null,
    description = null,
    airedOn = null,
    releasedOn = null,
    updatedAt = now
)

fun AnimeDetailsDto.toEntity(now: Long): AnimeEntity = AnimeEntity(
    id = id,
    name = name,
    russian = russian,
    imagePreviewUrl = fullImageUrl(image?.preview),
    imageOriginalUrl = fullImageUrl(image?.original),
    score = score.toScoreDoubleOrNull(),
    status = status,
    episodes = episodes,
    episodesAired = episodesAired,
    description = description,
    airedOn = airedOn,
    releasedOn = releasedOn,
    updatedAt = now
)

fun AnimeWithGenres.toDomain(): Anime = Anime(
    id = anime.id,
    name = anime.name,
    russian = anime.russian,
    imagePreviewUrl = anime.imagePreviewUrl,
    imageOriginalUrl = anime.imageOriginalUrl,
    score = anime.score,
    status = anime.status,
    episodes = anime.episodes,
    episodesAired = anime.episodesAired,
    description = anime.description,
    airedOn = anime.airedOn,
    releasedOn = anime.releasedOn,
    genres = genres.map { it.toDomain() }
)
