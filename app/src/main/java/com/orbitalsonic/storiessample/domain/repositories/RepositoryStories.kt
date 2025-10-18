package com.orbitalsonic.storiessample.domain.repositories

import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStory

interface RepositoryStories {
    suspend fun fetchStories()
    suspend fun getStories(): List<ItemStory>
}