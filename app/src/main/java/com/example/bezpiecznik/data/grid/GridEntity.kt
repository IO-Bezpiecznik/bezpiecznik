package com.example.bezpiecznik.data.grid

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grid_table")
data class GridEntity (
    // Grid id in API database
    @PrimaryKey val id: String
)