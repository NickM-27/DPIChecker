package com.nick.mowen.dpichecker.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nick.mowen.dpichecker.R
import com.nick.mowen.dpichecker.adapter.ChangelogAdapter
import java.util.*

class SettingsActivity : AbstractDpiActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (ads) {
            val adView = findViewById<AdView>(R.id.adView)
            MobileAds.initialize(this, getString(R.string.app_id))
            adView.loadAd(AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build())
        } else
            findViewById<View>(R.id.adView).visibility = View.GONE
    }

    override fun bindViews() {

    }

    override fun updateAds() {
        findViewById<View>(R.id.adView).visibility = View.GONE
    }

    fun getChangelog(@Suppress("UNUSED_PARAMETER") v: View) {
        AlertDialog.Builder(this)
                .setTitle("Whats new?")
                .setView(RecyclerView(this).apply {
                    setHasFixedSize(true)
                    adapter = ChangelogAdapter(this@SettingsActivity, changelogData)
                    layoutManager = LinearLayoutManager(this@SettingsActivity)
                })
                .setPositiveButton("RATE IN PLAY STORE") { _, _ ->
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=com.nick.mowen.sceneplugin".toUri()))
                    } catch (e: android.content.ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.nick.mowen.sceneplugin".toUri()))
                    }
                }.show()
    }

    fun contactDev(@Suppress("UNUSED_PARAMETER") view: View) {
        startActivity(Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("nickmowen213@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "DPI Checker Feedback")
            putExtra(Intent.EXTRA_TEXT, "I just wanted to tell you...")
        })
    }

    fun otherAppsDev(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(getString(R.string.dev_link))
        startActivity(intent)
    }

    fun supportDev(@Suppress("UNUSED_PARAMETER") view: View) {
        buyPremium()
    }

    fun gpsExplanation(@Suppress("UNUSED_PARAMETER") view: View) {
        AlertDialog.Builder(this).apply {
            setTitle("Google Play Services")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) setView(R.layout.dialog_gps_explanation) else setView(findViewById<View>(R.id.dialogGPSExplanation))
            show()
        }
    }

    private val changelogData: ArrayList<Array<String>>
        get() {
            val nums = resources.getStringArray(R.array.changelog_version)
            val texts = resources.getStringArray(R.array.changelog_texts)
            val finalArray = ArrayList<Array<String>>()
            for (i in nums.indices.reversed()) {
                val temp = arrayOf("", "")
                temp[0] = nums[i]
                temp[1] = texts[i]
                finalArray.add(temp)
            }
            return finalArray
        }
}