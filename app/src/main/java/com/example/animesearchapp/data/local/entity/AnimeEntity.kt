package com.example.animesearchapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animes")
data class AnimeEntity(
    @PrimaryKey val id: Int,
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
    val updatedAt: Long
)