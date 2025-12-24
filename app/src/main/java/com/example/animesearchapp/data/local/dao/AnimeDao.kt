package com.example.animesearchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.animesearchapp.data.local.entity.AnimeEntity
import com.example.animesearchapp.data.local.entity.AnimeGenreCrossRef
import com.example.animesearchapp.data.local.entity.GenreEntity
import com.example.animesearchapp.data.local.entity.QueryAnimeCrossRef
import com.example.animesearchapp.data.local.entity.SearchQueryEntity
import com.example.animesearchapp.data.local.entity.relations.AnimeWithGenres
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Upsert
    suspend fun upsertAnimes(items: List<AnimeEntity>)

    @Upsert
    suspend fun upsertAnime(item: AnimeEntity)

    @Upsert
    suspend fun upsertGenres(items: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAnimeGenreRefs(refs: List<AnimeGenreCrossRef>)

    @Query("DELETE FROM anime_genre_cross_ref WHERE animeId = :animeId")
    suspend fun deleteGenreRefsForAnime(animeId: Int)

    @Upsert
    suspend fun upsertSearchQuery(entity: SearchQueryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueryAnimeRefs(refs: List<QueryAnimeCrossRef>)

    @Query("DELETE FROM query_anime_cross_ref WHERE query = :query")
    suspend fun deleteQueryAnimeRefs(query: String)

    @Transaction
    @Query("""
        SELECT * FROM animes 
        WHERE id IN (SELECT animeId FROM query_anime_cross_ref WHERE query = :query)
        ORDER BY id DESC
    """)
    fun observeAnimesForQuery(query: String): Flow<List<AnimeWithGenres>>

    @Query("""
        SELECT query FROM search_queries
        ORDER BY lastUpdated DESC
        LIMIT :limit
    """)
    suspend fun getRecentQueries(limit: Int): List<String>

    @Query("""
        SELECT animeId FROM query_anime_cross_ref
        WHERE query = :query
        LIMIT 1
    """)
    suspend fun hasAnyAnimeForQuery(query: String): Int?

    @Query("SELECT lastUpdated FROM search_queries WHERE query = :query LIMIT 1")
    suspend fun getQueryLastUpdated(query: String): Long?

    @Transaction
    @Query("SELECT * FROM animes WHERE id = :animeId LIMIT 1")
    fun observeAnimeDetails(animeId: Int): Flow<AnimeWithGenres?>

    @Query("SELECT updatedAt FROM animes WHERE id = :animeId LIMIT 1")
    suspend fun getAnimeUpdatedAt(animeId: Int): Long?

    @Transaction
    suspend fun saveSearchResults(
        query: String,
        now: Long,
        animes: List<AnimeEntity>,
        genres: List<GenreEntity>,
        animeGenreRefs: List<AnimeGenreCrossRef>,
        queryAnimeRefs: List<QueryAnimeCrossRef>
    ) {
        upsertAnimes(animes)
        upsertGenres(genres)

        // refresh query<->anime
        deleteQueryAnimeRefs(query)
        insertQueryAnimeRefs(queryAnimeRefs)

        // refresh anime<->genres for involved anime ids
        val animeIds = animes.map { it.id }.distinct()
        for (id in animeIds) {
            deleteGenreRefsForAnime(id)
        }
        insertAnimeGenreRefs(animeGenreRefs)

        upsertSearchQuery(SearchQueryEntity(query = query, lastUpdated = now))
    }
}
