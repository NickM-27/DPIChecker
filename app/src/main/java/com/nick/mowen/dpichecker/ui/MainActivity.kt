package com.nick.mowen.dpichecker.ui

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nick.mowen.dpichecker.R
import com.nick.mowen.dpichecker.extension.checkDisclaimerStatus
import com.nick.mowen.dpichecker.extension.dpi

class MainActivity : AbstractDpiActivity() {

    private lateinit var layout: ConstraintLayout
    private lateinit var explanation: TextView
    private val explanationFrame = ConstraintSet()
    private var userDPI: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        checkDisclaimerStatus()

        if (ads) {
            val adView = findViewById<View>(R.id.adView) as AdView
            MobileAds.initialize(this, getString(R.string.app_id))
            adView.loadAd(AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build())
        } else
            findViewById<View>(R.id.adView).visibility = View.GONE
    }

    override fun bindViews() {
        setSupportActionBar(findViewById(R.id.toolbar))
        explanation = findViewById(R.id.explanation)
        layout = findViewById(R.id.container)
        explanationFrame.clone(this, R.layout.activity_main_keyframe)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun updateAds() {
        findViewById<View>(R.id.adView).visibility = View.GONE
    }

    fun checkDPI(@Suppress("UNUSED_PARAMETER") view: View) {
        userDPI = dpi
        TransitionManager.beginDelayedTransition(layout)
        explanation.text = String.format(getString(R.string.dpi_explanation), userDPI)
        explanationFrame.applyTo(layout)

        if (!ads)
            findViewById<View>(R.id.premium).visibility = View.GONE
        /*AlertDialog.Builder(this)
                .setOnDismissListener {

                }.setTitle("DPI Checker")
                .setMessage("This devices DPI is $userDPI\n$gpsString")
                .setPositiveButton("GO TO APKMIRROR.COM") { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.apkmirror.com/apk/google-inc/google-play-services/"))
                    startActivity(intent)
                }.setNegativeButton("DISMISS", null)
                .show()*/
    }

    fun purchasePremium(@Suppress("UNUSED_PARAMETER") view: View?) {
        buyPremium()
    }

    /*private val gpsVersion: Int
        get() {
            return when (userDPI) {
                160 -> 2
                240 -> 4
                320 -> 6
                480 -> 8
                else -> 0
            }
        }

    private val gpsString: String
        get() {
            return when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.KITKAT -> "If you want to install Google Play Services you will want your version to be 0X$gpsVersion "
                Build.VERSION_CODES.LOLLIPOP -> "If you want to install Google Play Services you will want your version to be 2X$gpsVersion "
                Build.VERSION_CODES.M -> "If you want to install Google Play Services you will want your version to be 4X$gpsVersion "
                else -> "If you want to install Google Play Services you will want your version to be 4X$gpsVersion "
            }
        }*/
}