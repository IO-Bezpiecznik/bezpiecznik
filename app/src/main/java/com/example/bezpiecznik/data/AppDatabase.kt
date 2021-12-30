package com.example.bezpiecznik.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bezpiecznik.data.code.CodeDao
import com.example.bezpiecznik.data.code.CodeEntity
import com.example.bezpiecznik.data.grid.GridDao
import com.example.bezpiecznik.data.grid.GridEntity

@Database(entities = [GridEntity::class, CodeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gridDao(): GridDao
    abstract fun codeDao(): CodeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun initializeConnection(context: Context): AppDatabase {
            if (INSTANCE != null) return INSTANCE!!
            synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, AppDatabase::class.java, "bezpiecznik")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!
            }
        }

        fun getConnection(): AppDatabase {
            return INSTANCE!!
        }
    }
}