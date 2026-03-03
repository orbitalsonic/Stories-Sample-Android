package dev.epegasus.storyview.listeners

interface StoriesListener {
    fun onNext()
    fun onPrev()
    fun onComplete()
}