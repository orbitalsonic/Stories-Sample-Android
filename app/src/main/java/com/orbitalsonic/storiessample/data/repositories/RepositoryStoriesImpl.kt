package com.orbitalsonic.storiessample.data.repositories

import com.orbitalsonic.storiessample.data.dataSources.local.DataSourceLocalStories
import com.orbitalsonic.storiessample.data.entities.ItemStory
import com.orbitalsonic.storiessample.domain.repositories.RepositoryStories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryStoriesImpl(private val dataSource: DataSourceLocalStories) : RepositoryStories {

    override suspend fun getStories(): List<ItemStory> = withContext(Dispatchers.IO) {
        dataSource.getStories()
    }
}