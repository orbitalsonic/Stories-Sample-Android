package com.orbitalsonic.storiessample.data.dataSources.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.orbitalsonic.storiessample.data.entities.ItemStory
import com.orbitalsonic.storiessample.data.entities.StoriesResponse
import com.orbitalsonic.storiessample.data.entities.StoryCategory
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import dev.epegasus.storyview.dataClasses.MyStory
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader

class DataSourceLocalStories(private val context: Context) {

    @Volatile
    private var storiesResponse: StoriesResponse

    private val cacheFile = File(context.filesDir, "stories.json")

    init {
        // Try to load cached file if exists (persisted between app launches)
        storiesResponse = if (cacheFile.exists()) {
            try {
                FileReader(cacheFile).use { reader ->
                    Gson().fromJson(reader, StoriesResponse::class.java)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to load cache, falling back to assets", ex)
                loadFromAssets()
            }
        } else {
            loadFromAssets()
        }
    }

    private fun loadFromAssets(): StoriesResponse {
        return try {
            context.assets.open("stories.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val storyCategories = Gson().fromJson(reader, Array<StoryCategory>::class.java)
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
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "DataSourceLocalStories: Failed to load stories JSON from assets", ex)
            StoriesResponse(emptyList())
        }
    }

    /** Called by repository to update local cache with remote data */
    fun updateStoriesResponse(newResponse: StoriesResponse) {
        storiesResponse = newResponse
        try {
            FileWriter(cacheFile).use { writer ->
                Gson().toJson(newResponse, writer)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "DataSourceLocalStories: Failed to save reels cache", ex)
        }
    }

    fun getStories(): List<ItemStory> {
        Log.d(TAG, "DataSourceLocalStories: getCategoryList: called")
        return storiesResponse.categories
    }
}