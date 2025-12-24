package com.example.animesearchapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("russian") val russian: String? = null,
    @SerialName("kind") val kind: String? = null
)