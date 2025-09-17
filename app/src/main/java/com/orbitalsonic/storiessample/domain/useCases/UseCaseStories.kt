package com.orbitalsonic.storiessample.domain.useCases

import com.orbitalsonic.storiessample.data.entities.ItemStory
import com.orbitalsonic.storiessample.data.repositories.RepositoryStoriesImpl

class UseCaseStories(private val repository: RepositoryStoriesImpl) {

    suspend fun getStories(): List<ItemStory> = repository.getStories()

}