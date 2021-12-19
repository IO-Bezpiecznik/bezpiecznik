package com.example.bezpiecznik.types

import kotlinx.serialization.Serializable

@Serializable
data class CodeCreateDto (
    val gridId: String,
    val points: Int,
    val username: String,
    val pattern: String,
)