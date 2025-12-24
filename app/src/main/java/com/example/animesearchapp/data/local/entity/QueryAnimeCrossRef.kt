package com.example.animesearchapp.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "query_anime_cross_ref",
    primaryKeys = ["query", "animeId"]
)
data class QueryAnimeCrossRef(
    val query: String,
    val animeId: Int
)