package com.orbitalsonic.storiessample.utilities.extensions

import android.view.View


fun View.pulseAttention(scale: Float = 1.1f, duration: Long = 150L, repeat: Int = 1) {
    // cancel any previous animation
    animate().cancel()

    val animatorUp = animate()
        .scaleX(scale)
        .scaleY(scale)
        .setDuration(duration)

    animatorUp.withEndAction {
        val animatorDown = animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
        animatorDown.withEndAction {
            if (repeat > 1) {
                postDelayed({ pulseAttention(scale, duration, repeat - 1) }, 0)
            }
        }
        animatorDown.start()
    }
    animatorUp.start()
}

fun View.flashAttention(duration: Long = 300L) {
    animate().alpha(0.3f).setDuration(duration).withEndAction {
        animate().alpha(1f).setDuration(duration).start()
    }.start()
}