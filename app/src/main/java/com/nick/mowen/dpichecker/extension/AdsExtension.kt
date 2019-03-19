package com.nick.mowen.dpichecker.extension

import android.app.Activity
import android.app.AlertDialog
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.google.ads.consent.ConsentForm
import com.google.ads.consent.ConsentFormListener
import com.google.ads.consent.ConsentStatus
import java.net.MalformedURLException
import java.net.URL

fun Activity.checkDisclaimerStatus() {
    if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("disclaimer", false)) {
        AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Disclaimer")
                .setMessage("DPI Checker is an app for android devices. It uses Display Metrics with the Window Manager Service to determine the devices DPI. This method is very accurate but there is always the possibility that in a rare occurrence there could be an error and an incorrect value is given.\n\nIf you proceed to install the incorrect version of an app, threre is the possibility that your Google Apps will stop working until you re-install the correct version\n\nNeither me, nor any party involved in the creation of this app guarantee that the suggested app version is correct")
                .setPositiveButton("ACCEPT") { _, _ ->
                    PreferenceManager.getDefaultSharedPreferences(this).edit { putBoolean("disclaimer", true) }
                    getAdsConsent()
                }.setNegativeButton("DECLINE") { _, _ ->
                    finish()
                }.show()
    }
}

fun Activity.getAdsConsent() {
    val privacyUrl: URL

    try {
        privacyUrl = URL("http://www.nicknackdevelopment.com/privacy-policy.html")
    } catch (e: MalformedURLException) {
        e.printStackTrace()
        return
    }

    ConsentForm.Builder(this, privacyUrl)
            .withListener(object : ConsentFormListener() {
                override fun onConsentFormLoaded() {

                }

                override fun onConsentFormOpened() {

                }

                override fun onConsentFormClosed(consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?) {

                }

                override fun onConsentFormError(reason: String?) {

                }
            }).withPersonalizedAdsOption()
            .withNonPersonalizedAdsOption()
            .withAdFreeOption()
            .build()
            .show()
}