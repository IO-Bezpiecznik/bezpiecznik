package com.example.bezpiecznik.data.grid

import androidx.room.*
import com.example.bezpiecznik.data.code.CodeEntity

@Dao
interface GridDao {
    @Insert
    fun insert(grid: GridEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(vararg grid: GridEntity)

    @Delete
    fun delete(grid: GridEntity)

    @Query("SELECT * FROM grid_table")
    fun getAll(): List<GridEntity>

    @Query("SELECT * FROM grid_table WHERE id = :id")
    fun getById(id: String): GridEntity
}