package com.orbitalsonic.storiessample.utilities.extensions

import com.orbitalsonic.storiessample.presentation.models.Category
import com.orbitalsonic.storiessample.presentation.models.ItemStoryCategoryLib
import com.orbitalsonic.storiessample.presentation.models.Story
import dev.epegasus.storyview.dataClasses.MyStory
import java.util.Date

fun Category.toItemStoryCategory(): ItemStoryCategoryLib {

    return ItemStoryCategoryLib(
        id = id,
        headerText = title,
        subHeaderText = subTitle,
        headerUrl = imageUrl,
        storyList = stories.map { it.toMyStory() }
    )
}

fun Story.toMyStory(): MyStory {

    return MyStory(
        url = url,
        date = Date(),
        description = null
    )
}

fun List<Category>.toItemStoryCategory(): List<ItemStoryCategoryLib> {
    return map { it.toItemStoryCategory() }
}