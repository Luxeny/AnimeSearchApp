package com.example.animesearchapp.domain.model

data class Genre(
    val id: Int,
    val name: String,
    val russian: String?,
    val kind: String?
) {
    val displayName: String get() = russian?.takeIf { it.isNotBlank() } ?: name
}