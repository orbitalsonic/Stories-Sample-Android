package dev.epegasus.storyview.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

object GeneralUtils {

    fun Context.getActivity(): Activity? {
        return when (this) {
            is Activity -> this
            is ContextWrapper -> this.baseContext.getActivity()
            else -> null
        }
    }
}