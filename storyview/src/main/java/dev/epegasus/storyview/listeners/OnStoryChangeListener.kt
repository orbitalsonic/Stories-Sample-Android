package dev.epegasus.storyview.listeners

interface OnStoryChangeListener {
    fun storyChanged(position: Int)
    fun storySwiped(swipeDirection: Int)  // 0: if swiped from left to right, 1: if swiped from right to left
    fun storyDismiss()
}