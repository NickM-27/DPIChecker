package com.nick.mowen.dpichecker.extension

import android.app.Activity
import android.util.DisplayMetrics

val Activity.dpi: Int
    get() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.densityDpi
    }