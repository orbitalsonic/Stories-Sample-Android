package com.orbitalsonic.storiessample.data.repositories

import android.util.Log
import com.orbitalsonic.storiessample.data.dataSources.entities.ItemStory
import com.orbitalsonic.storiessample.data.dataSources.local.DataSourceLocalStories
import com.orbitalsonic.storiessample.data.dataSources.remote.DataSourceRemoteStories
import com.orbitalsonic.storiessample.domain.repositories.RepositoryStories
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryStoriesImpl(private val dataSourceLocal: DataSourceLocalStories, private val dataSourceRemote: DataSourceRemoteStories) : RepositoryStories {

    override suspend fun fetchStories(): Unit = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "RepositoryStoriesImpl: fetchRemote: starting remote fetch")
            val remoteStories = dataSourceRemote.checkRemoteValues()
            if (remoteStories != null) {
                dataSourceLocal.updateStoriesResponse(remoteStories)
                Log.d(TAG, "RepositoryStoriesImpl: fetchRemote: local cache updated with remote data")
            } else {
                Log.d(TAG, "RepositoryStoriesImpl: fetchRemote: no remote data available (keeping local)")
            }
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryStoriesImpl: fetchRemote: failed to fetch remote data", ex)
        }
    }

    override suspend fun getStories(): List<ItemStory> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "RepositoryStoriesImpl: getStories: fetching stories from local data source")
            val stories = dataSourceLocal.getStories()
            Log.d(TAG, "RepositoryStoriesImpl: getStories: retrieved ${stories.size} stories")
            stories
        } catch (ex: Exception) {
            Log.e(TAG, "RepositoryStoriesImpl: getStories: failed to get stories", ex)
            emptyList()
        }
    }
}