package com.orbitalsonic.storiessample.data.local

import android.content.Context
import com.google.gson.Gson
import com.orbitalsonic.storiessample.presentation.models.Category
import com.orbitalsonic.storiessample.presentation.models.Story

class StoryLocalDataSource(private val context: Context) {

    fun getAllCategories(): List<Category> {
        val json = context.assets.open("stories.json")
            .bufferedReader()
            .use { it.readText() }

        val gson = Gson()
        val root = gson.fromJson(json, Root::class.java)

        return root.categories.map {
            Category(
                id = it.id,
                title = it.title,
                subtitle = it.subtitle,
                thumbnail = it.thumbnail,
                stories = it.stories.map { s -> Story(s.imageUrl) }
            )
        }
    }

    data class Root(val categories: List<CategoryJson>)
    data class CategoryJson(
        val id: Int,
        val title: String,
        val subtitle: String,
        val thumbnail: String,
        val stories: List<StoryJson>
    )

    data class StoryJson(val imageUrl: String)
}