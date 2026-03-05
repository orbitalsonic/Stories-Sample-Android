package com.orbitalsonic.storiessample.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.orbitalsonic.storiessample.data.db.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category_seen")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category_seen WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: CategoryEntity)

    @Query("DELETE FROM category_seen WHERE savedDate < :todayDate")
    suspend fun clearExpired(todayDate: Long)
}