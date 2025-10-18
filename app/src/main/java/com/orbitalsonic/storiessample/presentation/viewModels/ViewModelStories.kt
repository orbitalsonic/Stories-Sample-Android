package com.orbitalsonic.storiessample.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbitalsonic.storiessample.data.entities.ItemStory
import com.orbitalsonic.storiessample.domain.useCases.UseCaseStories
import kotlinx.coroutines.launch

class ViewModelStories(private val useCase: UseCaseStories) : ViewModel() {

    private val _listLiveData = MutableLiveData<List<ItemStory>>()
    val listLiveData: LiveData<List<ItemStory>> = _listLiveData
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchList()
    }

    private fun fetchList() = viewModelScope.launch {
        _isLoading.value = true
        _listLiveData.value = useCase.getStories()
        _isLoading.value = false
    }
    
    fun refreshStories() = viewModelScope.launch {
        _isLoading.value = true
        _listLiveData.value = useCase.refreshStories()
        _isLoading.value = false
    }
}