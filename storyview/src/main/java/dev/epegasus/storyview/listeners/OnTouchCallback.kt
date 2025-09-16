package dev.epegasus.storyview.listeners

/**
 * Created by Sohaib Ahmed on 02/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

interface OnTouchCallback {
    fun touchUp()
    fun touchPull()
    fun touchDown(xValue: Float, yValue: Float)
    fun touchHorizontalSwipe(swipeDirection: Int) // 0: if swiped from left to right, 1: if swiped from right to left
}