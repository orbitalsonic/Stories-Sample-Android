package com.orbitalsonic.storiessample.utilities.extensions

import android.content.Context
import android.widget.Toast

fun Context?.showToast(message: String) {
    this ?: return
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context?.showToast(messageResId: Int) {
    this ?: return
    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
}