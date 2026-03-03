package dev.epegasus.storyview.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import dev.epegasus.storyview.listeners.OnTouchCallback
import dev.epegasus.storyview.listeners.pull_dismiss_listener.OnPullDismissListener
import kotlin.math.abs

class PullDismissLayout : FrameLayout {

    private var onPullDismissListener: OnPullDismissListener? = null
    private var onTouchCallback: OnTouchCallback? = null
    private var viewDragHelper: ViewDragHelper? = null

    private val minSwipeDistance = 100 // Adjust this threshold as needed (in pixels)
    private var isSwipeDetected = false // Flag to ensure swipe is only detected once
    private var verticalTouchSlop = 0f
    private var horizontalTouchSlop = 0f
    private var minFlingVelocity = 0f
    private var animateAlpha = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        val viewConfiguration = ViewConfiguration.get(context)
        minFlingVelocity = viewConfiguration.scaledMinimumFlingVelocity.toFloat()
        viewDragHelper = ViewDragHelper.create(this, ViewDragCallback(this))
    }

    fun setMinFlingVelocity(velocity: Float) {
        this.minFlingVelocity = velocity
    }

    fun setAnimateAlpha(alpha: Boolean) {
        this.animateAlpha = alpha
    }

    fun setListener(listener: OnPullDismissListener?) {
        this.onPullDismissListener = listener
    }

    fun setTouchCallbacks(callback: OnTouchCallback) {
        this.onTouchCallback = callback
    }

    override fun computeScroll() {
        super.computeScroll()
        if (viewDragHelper?.continueSettling(true) == true) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(event)
        var pullingDown = false
        var direction = -1

        viewDragHelper?.let { viewDragHelper ->
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    // Reset the flag on a new touch event
                    isSwipeDetected = false

                    // Store initial touch positions
                    verticalTouchSlop = event.y
                    horizontalTouchSlop = event.x
                    val dy = event.y - verticalTouchSlop
                    val dx = event.x - horizontalTouchSlop

                    // If user is dragging vertically, start pulling down
                    if (dy > viewDragHelper.touchSlop) {
                        pullingDown = true
                        onTouchCallback?.touchPull()
                    }
                    // Detect horizontal drag (if dx > threshold)
                    else if (dx > viewDragHelper.touchSlop) {
                        if (abs(dx) > minSwipeDistance && !isSwipeDetected) {
                            isSwipeDetected = true // Lock swipe detection
                            val swipeDirection = if (dx > 0) 0 else 1 // 0 = left-to-right, 1 = right-to-left
                            onTouchCallback?.touchHorizontalSwipe(swipeDirection)
                        }
                    } else {
                        // Initial touch event
                        onTouchCallback?.touchDown(event.x, event.y)
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - horizontalTouchSlop
                    val dy = event.y - verticalTouchSlop

                    // Vertical drag logic
                    if (abs(dy) > viewDragHelper.touchSlop && abs(dy) > abs(dx)) {
                        pullingDown = true
                        onTouchCallback?.touchPull()
                    }
                    // Horizontal drag logic with swipe threshold
                    else if (abs(dx) > viewDragHelper.touchSlop && abs(dx) > abs(dy)) {
                        if (abs(dx) > minSwipeDistance && !isSwipeDetected) {
                            isSwipeDetected = true // Lock swipe detection
                            val swipeDirection = if (dx > 0) 0 else 1 // 0 = left-to-right, 1 = right-to-left
                            onTouchCallback?.touchHorizontalSwipe(swipeDirection)
                        }
                    } else {
                        // Update the initial touch down event
                        onTouchCallback?.touchDown(event.x, event.y)
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Reset the swipe detection flag
                    isSwipeDetected = false

                    // Reset touch slop values
                    verticalTouchSlop = 0.0f
                    horizontalTouchSlop = 0.0f
                    onTouchCallback?.touchUp()
                }
            }

            // Pull-dismiss logic
            onPullDismissListener?.let { pullDismissListener ->
                if (!viewDragHelper.shouldInterceptTouchEvent(event) && pullingDown) {
                    if (viewDragHelper.viewDragState == ViewDragHelper.STATE_IDLE &&
                        viewDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL)
                    ) {
                        val child = getChildAt(0)
                        if (child != null && !pullDismissListener.onShouldInterceptTouchEvent()) {
                            viewDragHelper.captureChildView(child, event.getPointerId(0))
                            return viewDragHelper.viewDragState == ViewDragHelper.STATE_DRAGGING
                        }
                    }
                }
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper?.processTouchEvent(event)
        return viewDragHelper?.capturedView != null
    }

    private class ViewDragCallback(private val pullDismissLayout: PullDismissLayout) : ViewDragHelper.Callback() {

        private var capturedView: View? = null
        private var dragPercent = 0.0f
        private var startTop = 0
        private var dismissed = false

        override fun tryCaptureView(view: View, i: Int): Boolean {
            return capturedView == null
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return if (top < 0) 0 else top
        }

        override fun onViewCaptured(view: View, activePointerId: Int) {
            capturedView = view
            startTop = view.top
            dragPercent = 0.0f
            dismissed = false
        }

        @SuppressLint("NewApi")
        override fun onViewPositionChanged(view: View, left: Int, top: Int, dx: Int, dy: Int) {
            val range = pullDismissLayout.height
            val moved = abs(top - startTop)
            if (range > 0) {
                dragPercent = moved.toFloat() / range.toFloat()
            }
            if (pullDismissLayout.animateAlpha) {
                view.alpha = 1.0f - dragPercent
                pullDismissLayout.invalidate()
            }
        }

        override fun onViewDragStateChanged(state: Int) {
            if (capturedView != null && dismissed && state == ViewDragHelper.STATE_IDLE) {
                pullDismissLayout.removeView(capturedView)
                pullDismissLayout.onPullDismissListener?.onDismissed()
            }
        }

        override fun onViewReleased(view: View, xv: Float, yv: Float) {
            dismissed = dragPercent >= 0.50f || abs(xv) > pullDismissLayout.minFlingVelocity && dragPercent > 0.20f
            val finalTop = if (dismissed) pullDismissLayout.height else startTop
            if (!dismissed) {
                pullDismissLayout.getTouchCallbacks()?.touchUp()
            }
            pullDismissLayout.viewDragHelper?.settleCapturedViewAt(0, finalTop)
            pullDismissLayout.invalidate()
        }
    }

    fun getTouchCallbacks(): OnTouchCallback? {
        return onTouchCallback
    }
}