package com.example.bezpiecznik.types

import kotlinx.serialization.Serializable

@Serializable
data class GridCreateDto (
    // JSON string
    val board: String
)