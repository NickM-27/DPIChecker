package com.nick.mowen.dpichecker.binding

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter

object BindingAdapter {

    @BindingAdapter("layoutFullscreen")
    @JvmStatic
    fun View.bindLayoutFullscreen(previousFullscreen: Boolean, fullscreen: Boolean) {
        if (previousFullscreen != fullscreen && fullscreen) {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    @BindingAdapter(
        "paddingLeftSystemWindowInsets",
        "paddingTopSystemWindowInsets",
        "paddingRightSystemWindowInsets",
        "paddingBottomSystemWindowInsets",
        requireAll = false
    )
    @JvmStatic
    fun View.applySystemWindowInsetsPadding(
        previousApplyLeft: Boolean,
        previousApplyTop: Boolean,
        previousApplyRight: Boolean,
        previousApplyBottom: Boolean,
        applyLeft: Boolean,
        applyTop: Boolean,
        applyRight: Boolean,
        applyBottom: Boolean
    ) {
        if (previousApplyLeft == applyLeft &&
            previousApplyTop == applyTop &&
            previousApplyRight == applyRight &&
            previousApplyBottom == applyBottom
        ) {
            return
        }

        doOnApplyWindowInsets { view, insets, padding, _ ->
            val left = if (applyLeft) insets.systemWindowInsetLeft else 0
            val top = if (applyTop) insets.systemWindowInsetTop else 0
            val right = if (applyRight) insets.systemWindowInsetRight else 0
            val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

            view.setPadding(
                padding.left + left,
                padding.top + top,
                padding.right + right,
                padding.bottom + bottom
            )
        }
    }

    @BindingAdapter(
        "marginLeftSystemWindowInsets",
        "marginTopSystemWindowInsets",
        "marginRightSystemWindowInsets",
        "marginBottomSystemWindowInsets",
        requireAll = false
    )
    @JvmStatic
    fun View.applySystemWindowInsetsMargin(
        previousApplyLeft: Boolean,
        previousApplyTop: Boolean,
        previousApplyRight: Boolean,
        previousApplyBottom: Boolean,
        applyLeft: Boolean,
        applyTop: Boolean,
        applyRight: Boolean,
        applyBottom: Boolean
    ) {
        if (previousApplyLeft == applyLeft &&
            previousApplyTop == applyTop &&
            previousApplyRight == applyRight &&
            previousApplyBottom == applyBottom
        ) {
            return
        }

        doOnApplyWindowInsets { view, insets, _, margin ->
            val left = if (applyLeft) insets.systemWindowInsetLeft else 0
            val top = if (applyTop) insets.systemWindowInsetTop else 0
            val right = if (applyRight) insets.systemWindowInsetRight else 0
            val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = margin.left + left
                topMargin = margin.top + top
                rightMargin = margin.right + right
                bottomMargin = margin.bottom + bottom
            }
        }
    }

    private fun View.doOnApplyWindowInsets(block: (View, WindowInsetsCompat, InitialPadding, InitialMargin) -> Unit) {
        // Create a snapshot of the view's padding & margin states
        val initialPadding = recordInitialPaddingForView(this)
        val initialMargin = recordInitialMarginForView(this)
        // Set an actual OnApplyWindowInsetsListener which proxies to the given
        // lambda, also passing in the original padding & margin states
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            block(v, insets, initialPadding, initialMargin)
            // Always return the insets, so that children can also use them
            insets
        }
        // request some insets
        requestApplyInsetsWhenAttached()
    }

    class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

    class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)

    private fun recordInitialPaddingForView(view: View) = InitialPadding(
        view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
    )

    private fun recordInitialMarginForView(view: View): InitialMargin {
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
            ?: throw IllegalArgumentException("Invalid view layout params")
        return InitialMargin(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
    }

    private fun View.requestApplyInsetsWhenAttached() {
        if (isAttachedToWindow) {
            // We're already attached, just request as normal
            ViewCompat.requestApplyInsets(this)
        } else {
            // We're not attached to the hierarchy, add a listener to
            // request when we are
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    v.removeOnAttachStateChangeListener(this)
                    ViewCompat.requestApplyInsets(v)
                }

                override fun onViewDetachedFromWindow(v: View) = Unit
            })
        }
    }
}