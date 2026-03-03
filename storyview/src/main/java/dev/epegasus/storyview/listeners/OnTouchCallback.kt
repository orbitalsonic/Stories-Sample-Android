package dev.epegasus.storyview.listeners

interface OnTouchCallback {
    fun touchUp()
    fun touchPull()
    fun touchDown(xValue: Float, yValue: Float)
    fun touchHorizontalSwipe(swipeDirection: Int) // 0: if swiped from left to right, 1: if swiped from right to left
}