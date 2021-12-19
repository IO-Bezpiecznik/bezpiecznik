package com.example.bezpiecznik.data.code

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "code_table")
data class CodeEntity (
    // Code id in API database
    @PrimaryKey val id: String
)