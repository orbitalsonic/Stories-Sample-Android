package com.orbitalsonic.storiessample.presentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbitalsonic.storiessample.data.dataSources.entities.ItemStory
import com.orbitalsonic.storiessample.domain.useCases.UseCaseStories
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import kotlinx.coroutines.launch

/**
 * ViewModel for ActivityStories
 * Handles story list display and navigation
 */
class ViewModelStories(private val useCase: UseCaseStories) : ViewModel() {

    private val _listLiveData = MutableLiveData<List<ItemStory>>()
    val listLiveData: LiveData<List<ItemStory>> = _listLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchList()
    }

    /**
     * Fetch stories list (same sorted order as MainActivity for ViewPager)
     */
    private fun fetchList() = viewModelScope.launch {
        _isLoading.value = true
        
        useCase.getStoriesWithSeenStatus()
            .onSuccess { (stories, _) ->
                _listLiveData.value = stories
                Log.d(TAG, "ViewModelStories: fetchList: Loaded ${stories.size} sorted stories")
            }
            .onFailure { ex ->
                Log.e(TAG, "ViewModelStories: fetchList: Failed to load stories", ex)
            }
        
        _isLoading.value = false
    }

    /**
     * Refresh stories from remote source
     */
    fun refreshStories() = viewModelScope.launch {
        _isLoading.value = true
        
        useCase.refreshStories()
            .onSuccess { (stories, _) ->
                _listLiveData.value = stories
                Log.d(TAG, "ViewModelStories: refreshStories: Refreshed ${stories.size} sorted stories")
            }
            .onFailure { ex ->
                Log.e(TAG, "ViewModelStories: refreshStories: Failed to refresh stories", ex)
            }
        
        _isLoading.value = false
    }
}