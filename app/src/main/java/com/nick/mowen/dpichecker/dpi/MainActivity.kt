package com.nick.mowen.dpichecker.dpi

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.style.StyleSpan
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.databinding.DataBindingUtil
import com.nick.mowen.dpichecker.R
import com.nick.mowen.dpichecker.databinding.ActivityMainBinding
import com.nick.mowen.dpichecker.settings.SettingsActivity
import com.nick.mowen.dpichecker.skeleton.AbstractActivity

class MainActivity : AbstractActivity() {

    override lateinit var binding: ActivityMainBinding
    private val explanationFrame = ConstraintSet()
    private var userDPI: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        checkDisclaimerStatus()
    }

    override fun bindViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        explanationFrame.clone(this, R.layout.activity_main_keyframe)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun checkDPI(@Suppress("UNUSED_PARAMETER") view: View) {
        userDPI = dpi
        val dimens = res
        TransitionManager.beginDelayedTransition(binding.container)
        val text = String.format(getString(R.string.dpi_explanation), userDPI, dimens[0], dimens[1]).toSpannable()
        text[0..17] = StyleSpan(Typeface.BOLD)
        binding.explanation.text = text
        explanationFrame.applyTo(binding.container)

        if (premium)
            findViewById<View>(R.id.premium).visibility = View.GONE
    }

    fun purchasePremium(@Suppress("UNUSED_PARAMETER") view: View?) = buyPremium()

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