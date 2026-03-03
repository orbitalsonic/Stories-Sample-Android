package dev.epegasus.storyview.listeners.pull_dismiss_listener

interface OnPullDismissListener {

    /**
     * Layout is pulled down to dismiss
     * Good time to finish activity, remove fragment or any view
     */
    fun onDismissed()

    /**
     * Convenient method to avoid layout_color overriding event
     * If you have a RecyclerView or ScrollerView in our layout_color your can
     * avoid PullDismissLayout to handle event.
     *
     * @return true when ignore pull down event, f
     * false for allow PullDismissLayout handle event
     */
    fun onShouldInterceptTouchEvent(): Boolean
}