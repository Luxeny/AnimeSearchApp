package com.example.animesearchapp.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.animesearchapp.data.local.entity.AnimeEntity
import com.example.animesearchapp.data.local.entity.AnimeGenreCrossRef
import com.example.animesearchapp.data.local.entity.GenreEntity

data class AnimeWithGenres(
    @Embedded val anime: AnimeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AnimeGenreCrossRef::class,
            parentColumn = "animeId",
            entityColumn = "genreId"
        )
    )
    val genres: List<GenreEntity>
)