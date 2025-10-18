package com.orbitalsonic.storiessample.data.entities

import com.orbitalsonic.storiessample.data.dataSources.entities.ItemStory

data class StoriesResponse(
    val categories: List<ItemStory>
)

// Data classes to match the JSON structure in assets/stories.json
data class StoryItem(
    val imageUrl: String
)

data class StoryCategory(
    val category: String,
    val subHeader: String,
    val imageUrl: String,
    val stories: List<StoryItem>
)