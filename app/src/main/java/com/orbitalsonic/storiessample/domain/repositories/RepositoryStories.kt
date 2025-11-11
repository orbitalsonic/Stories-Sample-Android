package com.orbitalsonic.storiessample.domain.repositories

import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStoryCategoryLib

interface RepositoryStories {
    suspend fun fetchStories()
    suspend fun getStories(): List<ItemStoryCategoryLib>
}