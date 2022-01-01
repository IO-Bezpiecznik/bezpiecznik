package com.example.bezpiecznik.types

import com.example.bezpiecznik.data.code.CodeEntity
import com.example.bezpiecznik.data.grid.GridEntity
import kotlinx.serialization.Serializable

@Serializable
data class ExportSchema (
    val codes: List<CodeEntity>,
    val grids: List<GridEntity>,
)