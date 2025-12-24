package com.example.animesearchapp.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.animesearchapp.data.local.entity.AnimeEntity
import com.example.animesearchapp.data.local.entity.QueryAnimeCrossRef
import com.example.animesearchapp.data.local.entity.SearchQueryEntity

data class QueryWithAnimes(
    @Embedded val query: SearchQueryEntity,
    @Relation(
        parentColumn = "query",
        entityColumn = "id",
        associateBy = Junction(
            value = QueryAnimeCrossRef::class,
            parentColumn = "query",
            entityColumn = "animeId"
        )
    )
    val animes: List<AnimeEntity>
)