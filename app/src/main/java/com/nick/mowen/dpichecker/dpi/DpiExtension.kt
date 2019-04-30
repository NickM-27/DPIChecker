package com.nick.mowen.dpichecker.dpi

import android.app.Activity
import android.util.DisplayMetrics
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.preference.PreferenceManager

val Activity.dpi: Int
    get() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.densityDpi
    }

val Activity.res: IntArray
    get() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return intArrayOf(metrics.widthPixels, metrics.heightPixels)
    }

fun Activity.checkDisclaimerStatus() {
    if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("disclaimer", false)) {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Disclaimer")
            .setMessage("DPI Checker is an app for android devices. It uses Display Metrics with the Window Manager Service to determine the devices DPI. This method is very accurate but there is always the possibility that in a rare occurrence there could be an error and an incorrect value is given.\n\nIf you proceed to install the incorrect version of an app, there is the possibility that your Google Apps will stop working until you re-install the correct version\n\nNeither me, nor any party involved in the creation of this app guarantee that the suggested app version is correct")
            .setPositiveButton("ACCEPT") { _, _ ->
                PreferenceManager.getDefaultSharedPreferences(this).edit { putBoolean("disclaimer", true) }
            }.setNegativeButton("DECLINE") { _, _ ->
                finish()
            }.show()
    }
}