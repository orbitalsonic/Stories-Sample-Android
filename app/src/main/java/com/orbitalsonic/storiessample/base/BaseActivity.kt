package com.orbitalsonic.storiessample.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding>(private val bindingFactory: (LayoutInflater) -> T) :
    AppCompatActivity() {

    protected val binding by lazy { bindingFactory(layoutInflater) }
    protected var includeTopPadding = true
    protected var includeBottomPadding = true
    protected var enableKeyboardInsets = false
    protected var statusBarHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        onPreCreated()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setPadding()
        onCreated()
    }

    open fun onPreCreated() {}

    private fun setPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            when (enableKeyboardInsets) {
                true -> setPaddingKeyboard(v, insets)
                false -> setPaddingNormal(v, insets)
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setPaddingNormal(v: View, insets: WindowInsetsCompat) {
        val bars =
            insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())

        statusBarHeight = bars.top

        val topPadding = if (includeTopPadding) bars.top else 0
        val bottomPadding = if (includeBottomPadding) bars.bottom else 0

        v.updatePadding(
            left = bars.left,
            top = topPadding,
            right = bars.right,
            bottom = bottomPadding
        )
    }

    private fun setPaddingKeyboard(v: View, insets: WindowInsetsCompat) {
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())

        statusBarHeight = systemBars.top

        // Use the maximum of nav bar or IME for bottom padding
        val bottomPadding =
            if (includeBottomPadding) maxOf(systemBars.bottom, imeInsets.bottom) else 0
        val topPadding =
            if (includeTopPadding) maxOf(systemBars.top, cutout.top, statusBarHeight) else 0

        v.updatePadding(
            left = systemBars.left,
            top = topPadding,
            right = systemBars.right,
            bottom = bottomPadding
        )
    }

    /**
     * @param type
     *     0: Show SystemBars
     *     1: Hide StatusBars
     *     2: Hide NavigationBars
     *     3: Hide SystemBars
     */

    protected open fun hideStatusBar(type: Int) {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = when (type) {
                0 -> WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                else -> WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            when (type) {
                0 -> show(WindowInsetsCompat.Type.systemBars())
                1 -> hide(WindowInsetsCompat.Type.systemBars())
                2 -> hide(WindowInsetsCompat.Type.statusBars())
                3 -> hide(WindowInsetsCompat.Type.navigationBars())
                else -> hide(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    abstract fun onCreated()
}