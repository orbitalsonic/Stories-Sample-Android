package com.orbitalsonic.storiessample.domain.repositories

import com.orbitalsonic.storiessample.data.entities.ItemStory

interface RepositoryStories {
    suspend fun getStories(): List<ItemStory>
}