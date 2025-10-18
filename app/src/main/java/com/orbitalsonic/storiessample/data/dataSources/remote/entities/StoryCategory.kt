package com.orbitalsonic.storiessample.data.dataSources.remote.entities

data class StoryCategory(
    val category: String,
    val subHeader: String,
    val imageUrl: String,
    val stories: List<StoryItem>
)