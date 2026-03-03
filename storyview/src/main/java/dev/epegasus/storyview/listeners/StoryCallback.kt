package dev.epegasus.storyview.listeners

interface StoryCallback {
    fun startStories()
    fun pauseStories()
    fun nextStory()
    fun onDescriptionClickListener(position: Int)
    fun setDownloadButtonTouched(touched: Boolean)
}