// File: app/src/main/java/com/example/animesearchapp/data/repository/AnimeRepositoryImpl.kt
package com.example.animesearchapp.data.repository

import com.example.animesearchapp.core.AppConfig
import com.example.animesearchapp.core.result.AppResult
import com.example.animesearchapp.core.result.NetworkError
import com.example.animesearchapp.core.util.NetworkMonitor
import com.example.animesearchapp.core.util.safeApiCall
import com.example.animesearchapp.data.local.dao.AnimeDao
import com.example.animesearchapp.data.local.entity.AnimeGenreCrossRef
import com.example.animesearchapp.data.local.entity.QueryAnimeCrossRef
import com.example.animesearchapp.data.mapper.toDomain
import com.example.animesearchapp.data.mapper.toEntity
import com.example.animesearchapp.data.mapper.toEntity as genreToEntity
import com.example.animesearchapp.data.remote.ShikimoriApi
import com.example.animesearchapp.domain.model.Anime
import com.example.animesearchapp.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val api: ShikimoriApi,
    private val dao: AnimeDao,
    private val networkMonitor: NetworkMonitor
) : AnimeRepository {

    override fun observeSearchResults(query: String): Flow<List<Anime>> {
        return dao.observeAnimesForQuery(query).map { list -> list.map { it.toDomain() } }
    }

    override fun observeRecentResults(limit: Int): Flow<List<Anime>> = flow {
        val queries = dao.getRecentQueries(limit)
        if (queries.isEmpty()) {
            emit(emptyList())
            return@flow
        }
        val first = queries.first()
        emitAll(dao.observeAnimesForQuery(first).map { it.map { awg -> awg.toDomain() } })
    }

    override suspend fun refreshSearch(query: String, force: Boolean): AppResult<Unit>? {
        val q = query.trim()
        if (q.isEmpty()) return null

        val now = System.currentTimeMillis()
        val last = dao.getQueryLastUpdated(q)
        val hasCache = dao.hasAnyAnimeForQuery(q) != null
        val freshEnough = last != null && (now - last) < AppConfig.CACHE_TTL_MS

        if (!force && freshEnough) return null

        if (!networkMonitor.isOnline()) {
            return if (hasCache) AppResult.Error(NetworkError.NoInternet) else AppResult.Error(NetworkError.NoInternet)
        }

        val result = safeApiCall { api.searchAnimes(query = q) }
        if (result is AppResult.Success) {
            val dtos = result.data
            val animeEntities = dtos.map { it.toEntity(now) }

            val genreEntities = dtos
                .flatMap { it.genres }
                .distinctBy { it.id }
                .map { it.genreToEntity() }

            val animeGenreRefs = dtos.flatMap { dto ->
                dto.genres.map { g -> AnimeGenreCrossRef(animeId = dto.id, genreId = g.id) }
            }

            val queryAnimeRefs = dtos.map { dto -> QueryAnimeCrossRef(query = q, animeId = dto.id) }

            dao.saveSearchResults(
                query = q,
                now = now,
                animes = animeEntities,
                genres = genreEntities,
                animeGenreRefs = animeGenreRefs,
                queryAnimeRefs = queryAnimeRefs
            )
            return AppResult.Success(Unit)
        }
        return result as AppResult.Error
    }

    override fun observeAnimeDetails(animeId: Int): Flow<Anime?> {
        return dao.observeAnimeDetails(animeId).map { it?.toDomain() }
    }

    override suspend fun refreshAnimeDetails(animeId: Int, force: Boolean): AppResult<Unit>? {
        val now = System.currentTimeMillis()
        val updatedAt = dao.getAnimeUpdatedAt(animeId)
        val freshEnough = updatedAt != null && (now - updatedAt) < AppConfig.CACHE_TTL_MS

        if (!force && freshEnough) return null

        if (!networkMonitor.isOnline()) {
            return AppResult.Error(NetworkError.NoInternet)
        }

        val result = safeApiCall { api.getAnimeDetails(animeId) }
        if (result is AppResult.Success) {
            val dto = result.data
            val animeEntity = dto.toEntity(now)
            val genreEntities = dto.genres.distinctBy { it.id }.map { it.genreToEntity() }
            val refs = dto.genres.map { g -> AnimeGenreCrossRef(animeId = dto.id, genreId = g.id) }

            dao.upsertAnime(animeEntity)
            dao.upsertGenres(genreEntities)
            dao.deleteGenreRefsForAnime(dto.id)
            dao.insertAnimeGenreRefs(refs)

            return AppResult.Success(Unit)
        }
        return result as AppResult.Error
    }
}