package com.orbitalsonic.storiessample.data.dataSources.local.entities

import android.os.Parcelable
import dev.epegasus.storyview.dataClasses.MyStory
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemStoryCategoryLib(
    val id: Int,
    val headerText: String,
    val subHeaderText: String,
    val headerUrl: String,
    val storyList: List<MyStory>
) : Parcelable