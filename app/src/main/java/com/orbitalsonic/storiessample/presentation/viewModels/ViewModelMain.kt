package com.orbitalsonic.storiessample.presentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStoryCategoryLib
import com.orbitalsonic.storiessample.domain.useCases.UseCaseStories
import com.orbitalsonic.storiessample.domain.useCases.UseCaseStorySeen
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import kotlinx.coroutines.launch

class ViewModelMain(private val useCaseStories: UseCaseStories, private val useCaseStorySeen: UseCaseStorySeen) : ViewModel() {

    private val _storiesLiveData = MutableLiveData<List<ItemStoryCategoryLib>>()
    val storiesLiveData: LiveData<List<ItemStoryCategoryLib>> = _storiesLiveData

    private val _seenStatusLiveData = MutableLiveData<Map<Int, Boolean>>()
    val seenStatusLiveData: LiveData<Map<Int, Boolean>> = _seenStatusLiveData

    private val _navigateLiveData = MutableLiveData<Int>()
    val navigateLiveData: LiveData<Int> = _navigateLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        initializeApp()
    }

    /**
     * Initialize app with necessary setup
     */
    private fun initializeApp() = viewModelScope.launch {
        _isLoading.value = true
        
        try {
            // Setup daily reset and cleanup
            useCaseStorySeen.checkAndResetIfDateChanged()
            useCaseStorySeen.cleanupOldEntries()
            
            // Load stories
            loadStories()
        } catch (ex: Exception) {
            Log.e(TAG, "ViewModelMain: initializeApp: Failed to initialize app", ex)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Load stories with seen status
     */
    fun loadStories() = viewModelScope.launch {
        _isLoading.value = true
        
        useCaseStories.getStoriesWithSeenStatus()
            .onSuccess { (stories, seenStatus) ->
                _storiesLiveData.value = stories
                _seenStatusLiveData.value = seenStatus
                Log.d(TAG, "ViewModelMain: loadStories: Loaded ${stories.size} stories")
            }
            .onFailure { ex ->
                Log.e(TAG, "ViewModelMain: loadStories: Failed to load stories", ex)
            }
        
        _isLoading.value = false
    }

    /**
     * Handle story item click
     */
    fun onItemClick(itemStoryCategoryLib: ItemStoryCategoryLib) {
        // Find the index in the current sorted list (same as MainActivity display order)
        val currentStories = _storiesLiveData.value ?: emptyList()
        val sortedIndex = currentStories.indexOfFirst { it.id == itemStoryCategoryLib.id }
        val finalIndex = if (sortedIndex >= 0) sortedIndex else 0
        
        _navigateLiveData.value = finalIndex
        Log.d(TAG, "ViewModelMain: onItemClick: Navigating to story ${itemStoryCategoryLib.headerText} at sorted index $finalIndex")
    }

    /**
     * Refresh seen status and re-sort stories
     */
    fun refreshSeenStatus() = viewModelScope.launch {
        useCaseStories.refreshSeenStatus()
            .onSuccess { (stories, seenStatus) ->
                _storiesLiveData.value = stories
                _seenStatusLiveData.value = seenStatus
                Log.d(TAG, "ViewModelMain: refreshSeenStatus: Updated seen status")
            }
            .onFailure { ex ->
                Log.e(TAG, "ViewModelMain: refreshSeenStatus: Failed to refresh seen status", ex)
            }
    }

    /**
     * Cleanup old database entries
     */
    fun cleanupOldEntries() = viewModelScope.launch {
        try {
            useCaseStorySeen.cleanupOldEntries()
            Log.d(TAG, "ViewModelMain: cleanupOldEntries: Cleanup completed")
        } catch (ex: Exception) {
            Log.e(TAG, "ViewModelMain: cleanupOldEntries: Failed to cleanup old entries", ex)
        }
    }

    /**
     * Refresh stories from remote source
     */
    fun refreshStories() = viewModelScope.launch {
        _isLoading.value = true
        
        useCaseStories.refreshStories()
            .onSuccess { (stories, seenStatus) ->
                _storiesLiveData.value = stories
                _seenStatusLiveData.value = seenStatus
                Log.d(TAG, "ViewModelMain: refreshStories: Refreshed ${stories.size} stories")
            }
            .onFailure { ex ->
                Log.e(TAG, "ViewModelMain: refreshStories: Failed to refresh stories", ex)
            }
        
        _isLoading.value = false
    }
}
