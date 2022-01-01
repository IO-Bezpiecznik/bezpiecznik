package com.example.bezpiecznik.data.code

import androidx.room.*

@Dao
interface CodeDao {
    @Insert
    fun insert(code: CodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(vararg codes: CodeEntity)

    @Delete
    fun deleteAll(code: CodeEntity)

    @Query("SELECT * FROM code_table")
    fun getAll(): List<CodeEntity>

    @Query("SELECT * FROM code_table WHERE id = :id")
    fun getById(id: String): CodeEntity
}