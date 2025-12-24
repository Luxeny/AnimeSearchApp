package com.example.animesearchapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageDto(
    @SerialName("preview") val preview: String? = null,
    @SerialName("original") val original: String? = null
)