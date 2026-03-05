package com.orbitalsonic.storiessample.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.orbitalsonic.storiessample.data.repositories.StoryRepository
import com.orbitalsonic.storiessample.presentation.models.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    val parentCategories: LiveData<List<Category>> =
        repository.observeParentCategories()
            .flowOn(Dispatchers.IO)
            .asLiveData()

    fun getStoryList(title: String): LiveData<List<Category>> {

        val result = MutableLiveData<List<Category>>()

        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repository.getListFromCategoryTitle(title))
        }

        return result
    }

    fun markSeen(id: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            repository.markSeen(id)
        }
    }
}