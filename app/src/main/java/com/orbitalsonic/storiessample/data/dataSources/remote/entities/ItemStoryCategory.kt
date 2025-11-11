package com.orbitalsonic.storiessample.data.dataSources.remote.entities

data class ItemStoryCategory(
    val category: String,
    val subHeader: String,
    val imageUrl: String,
    val stories: List<ItemStory>
)