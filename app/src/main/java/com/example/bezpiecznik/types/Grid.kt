package com.example.bezpiecznik.types

import kotlinx.serialization.Serializable

@Serializable
data class Grid (
    val board: String,
    val _id: String,
    val __v: Int
)