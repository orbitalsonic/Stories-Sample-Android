package dev.epegasus.storyview.dataClasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HeaderInfo(
    var title: String? = null,
    var subtitle: String? = null,
    var titleIconUrl: String? = null
) : Parcelable