package com.orbitalsonic.storiessample.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_seen")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val isSeen: Boolean,
    val savedDate: Long
)