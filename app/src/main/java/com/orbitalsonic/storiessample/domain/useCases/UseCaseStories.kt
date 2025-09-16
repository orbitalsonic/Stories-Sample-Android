package com.orbitalsonic.storiessample.domain.useCases

import com.orbitalsonic.storiessample.data.entities.ItemStory
import com.orbitalsonic.storiessample.domain.repositories.RepositoryStories

class UseCaseStories(private val repository: RepositoryStories) {

    suspend fun getStories(): List<ItemStory> = repository.getStories()

}