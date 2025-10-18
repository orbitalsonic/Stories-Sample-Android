package com.orbitalsonic.storiessample.domain.repositories

interface RepositoryStorySeen {
    suspend fun isCategorySeen(categoryId: Int): Boolean
    suspend fun markStoryAsSeen(storyId: Int, categoryId: Int)
    suspend fun isStorySeen(storyId: Int): Boolean
    suspend fun resetAllSeenStories()
    suspend fun cleanupOldEntries()
    suspend fun checkAndResetIfDateChanged()
}
