package com.example.bezpiecznik.data.code

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CodeDao {
    @Insert
    fun insert(code: CodeEntity)

    @Delete
    fun delete(code: CodeEntity)

    @Query("SELECT * FROM code_table")
    fun getAll(): List<CodeEntity>

    @Query("SELECT * FROM code_table WHERE id = :id")
    fun getById(id: String): CodeEntity
}