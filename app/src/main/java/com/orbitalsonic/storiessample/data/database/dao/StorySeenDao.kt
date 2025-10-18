package com.orbitalsonic.storiessample.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.orbitalsonic.storiessample.data.database.entities.StorySeenEntity
import java.util.Date

@Dao
interface StorySeenDao {

    @Query("SELECT * FROM story_seen WHERE storyId = :storyId")
    suspend fun getStorySeenStatus(storyId: Int): StorySeenEntity?

    @Query("SELECT * FROM story_seen WHERE categoryId = :categoryId")
    suspend fun getCategorySeenStatus(categoryId: Int): List<StorySeenEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStorySeen(storySeen: StorySeenEntity)

    @Query("UPDATE story_seen SET isSeen = :isSeen, seenDate = :seenDate WHERE storyId = :storyId")
    suspend fun updateStorySeenStatus(storyId: Int, isSeen: Boolean, seenDate: Date)

    @Query("UPDATE story_seen SET isSeen = 0, seenDate = :resetDate WHERE categoryId = :categoryId")
    suspend fun resetCategorySeenStatus(categoryId: Int, resetDate: Date)

    @Query("UPDATE story_seen SET isSeen = 0, seenDate = :resetDate")
    suspend fun resetAllSeenStatus(resetDate: Date)

    @Query("DELETE FROM story_seen WHERE createdAt < :cutoffDate")
    suspend fun deleteOldEntries(cutoffDate: Date)

    @Query("SELECT COUNT(*) FROM story_seen WHERE categoryId = :categoryId AND isSeen = 1")
    suspend fun getSeenCountForCategory(categoryId: Int): Int

    @Query("SELECT COUNT(*) FROM story_seen WHERE categoryId = :categoryId")
    suspend fun getTotalCountForCategory(categoryId: Int): Int
}