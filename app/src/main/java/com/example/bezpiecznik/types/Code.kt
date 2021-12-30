package com.example.bezpiecznik.types

import kotlinx.serialization.Serializable

@Serializable
data class Code (
    val gridId: String,
    val points: Int,
    val username: String,
    val pattern: String,
    val _id: String,
    val __v: Int
)