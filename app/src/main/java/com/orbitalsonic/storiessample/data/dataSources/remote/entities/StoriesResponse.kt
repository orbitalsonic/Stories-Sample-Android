package com.orbitalsonic.storiessample.data.dataSources.remote.entities

import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStory

data class StoriesResponse(
    val categories: List<ItemStory>
)