package com.orbitalsonic.storiessample.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "story_seen")
data class StorySeenEntity(
    @PrimaryKey
    val storyId: Int,
    val categoryId: Int,
    val isSeen: Boolean = false,
    val seenDate: Date = Date(),
    val createdAt: Date = Date()
)