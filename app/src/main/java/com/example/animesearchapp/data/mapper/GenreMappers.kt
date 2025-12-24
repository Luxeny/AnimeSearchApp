package com.example.animesearchapp.data.mapper

import com.example.animesearchapp.data.local.entity.GenreEntity
import com.example.animesearchapp.data.remote.dto.GenreDto
import com.example.animesearchapp.domain.model.Genre

fun GenreDto.toEntity(): GenreEntity = GenreEntity(
    id = id,
    name = name,
    russian = russian,
    kind = kind
)

fun GenreEntity.toDomain(): Genre = Genre(
    id = id,
    name = name,
    russian = russian,
    kind = kind
)
