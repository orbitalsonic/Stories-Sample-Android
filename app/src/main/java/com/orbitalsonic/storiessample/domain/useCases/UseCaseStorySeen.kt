package com.orbitalsonic.storiessample.domain.useCases

import com.orbitalsonic.storiessample.domain.repositories.RepositoryStorySeen

class UseCaseStorySeen(private val repository: RepositoryStorySeen) {

    suspend fun isCategorySeen(categoryId: Int): Boolean {
        return repository.isCategorySeen(categoryId)
    }

    suspend fun markStoryAsSeen(storyId: Int, categoryId: Int) {
        repository.markStoryAsSeen(storyId, categoryId)
    }

    suspend fun isStorySeen(storyId: Int): Boolean {
        return repository.isStorySeen(storyId)
    }
    
    suspend fun resetAllSeenStories() {
        repository.resetAllSeenStories()
    }
    
    suspend fun cleanupOldEntries() {
        repository.cleanupOldEntries()
    }
    
    suspend fun checkAndResetIfDateChanged() {
        repository.checkAndResetIfDateChanged()
    }
}
