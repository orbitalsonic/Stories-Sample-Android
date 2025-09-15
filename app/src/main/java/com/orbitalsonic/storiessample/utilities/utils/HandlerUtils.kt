package com.orbitalsonic.storiessample.utilities.utils

import android.os.Handler
import android.os.Looper

fun withDelay(delay: Long = 300L, block: () -> Unit) = Handler(Looper.getMainLooper()).postDelayed(block, delay)