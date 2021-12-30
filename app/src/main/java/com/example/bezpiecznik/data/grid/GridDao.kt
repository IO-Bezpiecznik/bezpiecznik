package com.example.bezpiecznik.data.grid

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GridDao {
    @Insert
    fun insert(grid: GridEntity)

    @Delete
    fun delete(grid: GridEntity)

    @Query("SELECT * FROM grid_table")
    fun getAll(): List<GridEntity>

    @Query("SELECT * FROM grid_table WHERE id = :id")
    fun getById(id: String): GridEntity
}