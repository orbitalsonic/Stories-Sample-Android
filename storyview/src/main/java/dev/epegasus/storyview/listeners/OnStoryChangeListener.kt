package dev.epegasus.storyview.listeners


/**
 * Created by Sohaib Ahmed on 02/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

interface OnStoryChangeListener {
    fun storyChanged(position: Int)
    fun storySwiped(swipeDirection: Int)  // 0: if swiped from left to right, 1: if swiped from right to left
}