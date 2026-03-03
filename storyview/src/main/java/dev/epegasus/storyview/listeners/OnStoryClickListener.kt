package dev.epegasus.storyview.listeners

interface OnStoryClickListener {
    fun onTitleIconClickListener(position: Int)
    fun onDescriptionClickListener(position: Int)
    fun onDownloadClickListener(position: Int, imageUrl: String)
}