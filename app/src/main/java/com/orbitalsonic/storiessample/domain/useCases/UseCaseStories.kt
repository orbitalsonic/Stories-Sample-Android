package com.orbitalsonic.storiessample.domain.useCases

import android.util.Log
import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStoryCategoryLib
import com.orbitalsonic.storiessample.domain.repositories.RepositoryStories
import com.orbitalsonic.storiessample.domain.repositories.RepositoryStorySeen
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UseCaseStories(private val repositoryStories: RepositoryStories, private val repositoryStorySeen: RepositoryStorySeen) {

    // Cache for original stories order (for navigation)
    private var originalStories = listOf<ItemStoryCategoryLib>()
    
    // Cache for current sorted stories
    private var currentStories = listOf<ItemStoryCategoryLib>()

    /**
     * Fetch stories from remote source
     */
    suspend fun fetchStories(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            repositoryStories.fetchStories()
            Log.d(TAG, "UseCaseStories: fetchStories: Successfully fetched stories from remote")
            Result.success(Unit)
        } catch (ex: Exception) {
            Log.e(TAG, "UseCaseStories: fetchStories: Failed to fetch stories", ex)
            Result.failure(ex)
        }
    }

    /**
     * Get stories with seen status and proper sorting
     * @return Pair of (sorted stories, seen status map)
     */
    suspend fun getStoriesWithSeenStatus(): Result<Pair<List<ItemStoryCategoryLib>, Map<Int, Boolean>>> = withContext(Dispatchers.Default) {
        try {
            val stories = repositoryStories.getStories()
            val seenStatusMap = getSeenStatusForStories(stories)
            val sortedStories = sortStoriesBySeenStatus(stories, seenStatusMap)
            
            // Update caches
            originalStories = stories.sortedBy { it.id }
            currentStories = sortedStories
            
            Log.d(TAG, "UseCaseStories: getStoriesWithSeenStatus: Loaded ${sortedStories.size} stories")
            Result.success(Pair(sortedStories, seenStatusMap))
        } catch (ex: Exception) {
            Log.e(TAG, "UseCaseStories: getStoriesWithSeenStatus: Failed to get stories", ex)
            Result.failure(ex)
        }
    }

    /**
     * Refresh stories and seen status
     */
    suspend fun refreshStories(): Result<Pair<List<ItemStoryCategoryLib>, Map<Int, Boolean>>> = withContext(Dispatchers.IO) {
        try {
            // Fetch from remote first
            fetchStories()
            // Then get updated stories
            getStoriesWithSeenStatus()
        } catch (ex: Exception) {
            Log.e(TAG, "UseCaseStories: refreshStories: Failed to refresh stories", ex)
            Result.failure(ex)
        }
    }

    /**
     * Get original unsorted stories for ViewPager
     * @return Original stories in JSON order
     */
    suspend fun getOriginalStories(): Result<List<ItemStoryCategoryLib>> = withContext(Dispatchers.IO) {
        try {
            if (originalStories.isEmpty()) {
                // Load stories first if not loaded
                val result = getStoriesWithSeenStatus()
                if (result.isSuccess) {
                    Log.d(TAG, "UseCaseStories: getOriginalStories: Loaded ${originalStories.size} original stories")
                    Result.success(originalStories)
                } else {
                    Result.failure(result.exceptionOrNull() ?: Exception("Failed to load stories"))
                }
            } else {
                Log.d(TAG, "UseCaseStories: getOriginalStories: Retrieved ${originalStories.size} original stories")
                Result.success(originalStories)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "UseCaseStories: getOriginalStories: Failed to get original stories", ex)
            Result.failure(ex)
        }
    }

    /**
     * Get original index for navigation
     * @param clickedStory The story that was clicked
     * @return Original index in the unsorted list
     */
    fun getOriginalIndexForNavigation(clickedStory: ItemStoryCategoryLib): Int {
        return originalStories.indexOfFirst { it.id == clickedStory.id }.takeIf { it >= 0 } ?: 0
    }

    /**
     * Get seen status for a list of stories
     */
    private suspend fun getSeenStatusForStories(stories: List<ItemStoryCategoryLib>): Map<Int, Boolean> {
        val seenStatusMap = mutableMapOf<Int, Boolean>()
        stories.forEach { story ->
            val isSeen = repositoryStorySeen.isCategorySeen(story.id)
            seenStatusMap[story.id] = isSeen
        }
        return seenStatusMap
    }

    /**
     * Sort stories: unseen first (maintaining original order), then seen (maintaining original order)
     */
    private fun sortStoriesBySeenStatus(
        stories: List<ItemStoryCategoryLib>,
        seenStatusMap: Map<Int, Boolean>
    ): List<ItemStoryCategoryLib> {
        return stories.sortedWith(compareBy<ItemStoryCategoryLib> { story ->
            val isSeen = seenStatusMap[story.id] ?: false
            if (isSeen) 1 else 0 // 0 for unseen (first), 1 for seen (second)
        }.thenBy { story ->
            story.id // Maintain original JSON order within each group
        })
    }

    /**
     * Re-sort current stories based on updated seen status
     */
    suspend fun refreshSeenStatus(): Result<Pair<List<ItemStoryCategoryLib>, Map<Int, Boolean>>> = withContext(Dispatchers.Default) {
        try {
            if (currentStories.isEmpty()) {
                Log.d(TAG, "UseCaseStories: refreshSeenStatus: No stories loaded, loading fresh stories")
                // If no stories are loaded, load them first
                return@withContext getStoriesWithSeenStatus()
            }
            
            val seenStatusMap = getSeenStatusForStories(currentStories)
            val sortedStories = sortStoriesBySeenStatus(currentStories, seenStatusMap)
            
            currentStories = sortedStories
            
            Log.d(TAG, "UseCaseStories: refreshSeenStatus: Updated seen status for ${sortedStories.size} stories")
            Result.success(Pair(sortedStories, seenStatusMap))
        } catch (ex: Exception) {
            Log.e(TAG, "UseCaseStories: refreshSeenStatus: Failed to refresh seen status", ex)
            Result.failure(ex)
        }
    }
}