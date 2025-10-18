package com.orbitalsonic.storiessample.presentation.ui.state

import com.orbitalsonic.storiessample.data.dataSources.entities.ItemStory

/**
 * UI state for story thumbnails
 */
data class StoryThumbnailState(
    val story: ItemStory,
    val isSeen: Boolean
)

/**
 * UI state for the main screen
 */
data class MainScreenState(
    val stories: List<StoryThumbnailState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
