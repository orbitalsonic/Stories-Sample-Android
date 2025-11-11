package com.orbitalsonic.storiessample.data.dataSources.remote.entities

import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStoryCategoryLib

data class StoriesResponse(
    val categories: List<ItemStoryCategoryLib>
)