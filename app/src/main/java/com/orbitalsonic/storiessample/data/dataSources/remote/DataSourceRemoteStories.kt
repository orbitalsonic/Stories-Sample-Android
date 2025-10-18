package com.orbitalsonic.storiessample.data.dataSources.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import com.orbitalsonic.storiessample.data.entities.ItemStory
import com.orbitalsonic.storiessample.data.entities.StoriesResponse
import com.orbitalsonic.storiessample.data.entities.StoryCategory
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import dev.epegasus.storyview.dataClasses.MyStory
import kotlinx.coroutines.tasks.await

class DataSourceRemoteStories {

    private val remoteConfig by lazy { Firebase.remoteConfig }

    init {
        // Set minimum fetch interval to 0 for real-time updates
        val configSettings = remoteConfigSettings {
            fetchTimeoutInSeconds = 10
            minimumFetchIntervalInSeconds = 0L
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    suspend fun checkRemoteValues(): StoriesResponse? {
        Log.d(TAG, "DataSourceRemoteStories: checkRemoteValues: called")

        return try {
            val response = remoteConfig.fetchAndActivate().await()
            Log.d(TAG, "DataSourceRemoteStories: checkRemoteValues: Result: $response")

            when (response) {
                true -> updateRemoteValues()
                false -> {
                    Log.d(TAG, "DataSourceRemoteStories: No new remote data available")
                    null
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "DataSourceRemoteStories: Failed to fetch remote config", ex)
            null
        }
    }

    private fun updateRemoteValues(): StoriesResponse? {
        return try {
            val json = remoteConfig.getString("stories_json")
            if (json.isEmpty()) {
                Log.w(TAG, "DataSourceRemoteStories: Remote stories_json is empty")
                return null
            }
            
            // Parse the remote JSON (should be in the same format as assets)
            val storyCategories = Gson().fromJson(json, Array<StoryCategory>::class.java)
            val itemStories = storyCategories.mapIndexed { index, category ->
                ItemStory(
                    id = index,
                    headerText = category.category,
                    subHeaderText = category.subHeader,
                    headerUrl = category.imageUrl,
                    storyList = category.stories.map { story ->
                        MyStory(url = story.imageUrl)
                    }
                )
            }
            StoriesResponse(itemStories)
        } catch (ex: Exception) {
            Log.e(TAG, "DataSourceRemoteStories: updateRemoteValues: Parsing remote JSON failed", ex)
            null
        }
    }
}