package com.orbitalsonic.storiessample.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.orbitalsonic.storiessample.data.db.dao.CategoryDao
import com.orbitalsonic.storiessample.data.db.entities.CategoryEntity

@Database(entities = [CategoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "story_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}