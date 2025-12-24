package com.example.animesearchapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.animesearchapp.data.local.dao.AnimeDao
import com.example.animesearchapp.data.local.entity.AnimeEntity
import com.example.animesearchapp.data.local.entity.AnimeGenreCrossRef
import com.example.animesearchapp.data.local.entity.GenreEntity
import com.example.animesearchapp.data.local.entity.QueryAnimeCrossRef
import com.example.animesearchapp.data.local.entity.SearchQueryEntity

@Database(
    entities = [
        SearchQueryEntity::class,
        AnimeEntity::class,
        GenreEntity::class,
        AnimeGenreCrossRef::class,
        QueryAnimeCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
}
