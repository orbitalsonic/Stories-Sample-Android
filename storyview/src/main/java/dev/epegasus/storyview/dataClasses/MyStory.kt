package dev.epegasus.storyview.dataClasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class MyStory(
    val url: String? = null,
    val date: Date? = null,
    val description: String? = null
) : Parcelable