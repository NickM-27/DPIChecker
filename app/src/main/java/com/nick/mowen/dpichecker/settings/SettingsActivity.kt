package com.nick.mowen.dpichecker.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nick.mowen.dpichecker.R
import com.nick.mowen.dpichecker.databinding.ActivitySettingsBinding
import com.nick.mowen.dpichecker.databinding.CustomRowInformationBinding
import com.nick.mowen.dpichecker.skeleton.AbstractActivity

class SettingsActivity : AbstractActivity() {

    override lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews()
    }

    override fun bindViews() {
        setContentView(R.layout.activity_settings)
    }

    fun playStore(@Suppress("UNUSED_PARAMETER") view: View?) =
        startActivity(Intent(Intent.ACTION_VIEW).apply { data = "https://play.google.com/store/apps/dev?id=6410686151642848556&hl=en".toUri() })

    fun rate(@Suppress("UNUSED_PARAMETER") view: View?) = rateAppOnPlayStore()

    fun share(@Suppress("UNUSED_PARAMETER") view: View?) {
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plan"
            //putExtra(Intent.EXTRA_TEXT, getString(R.string.action_share_app_with_link))
        }, getString(R.string.action_share_app)))
    }

    fun changeLog(@Suppress("UNUSED_PARAMETER") view: View?) = getChangelog()

    fun web(@Suppress("UNUSED_PARAMETER") view: View?) = try {
        startActivity(Intent(Intent.ACTION_VIEW).apply { data = getString(R.string.dev_web_link).toUri() })
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }

    fun contact(view: View?) = giveDevFeedback(view!!.context)

    fun privacy(@Suppress("UNUSED_PARAMETER") view: View?) = openPrivacyPolicy(this)

    fun sources(@Suppress("UNUSED_PARAMETER") view: View?) = getLicenses()

    private fun getChangelog() {
        AlertDialog.Builder(this)
            .setTitle("Whats new?")
            .setView(RecyclerView(this).also {
                it.setHasFixedSize(true)
                it.adapter = InformationAdapter(this, R.array.app_versions, R.array.app_changes)
                it.layoutManager = LinearLayoutManager(this)
                it.isVerticalScrollBarEnabled = true
            })
            .setPositiveButton(getString(R.string.action_rate_app)) { _, _ -> rateAppOnPlayStore() }
            .show()
    }

    private fun getLicenses() {
        AlertDialog.Builder(this)
            .setTitle("Licenses")
            .setView(RecyclerView(this).also {
                it.setHasFixedSize(true)
                //it.adapter = InformationAdapter(this, R.array.license_names, R.array.licenses)
                it.layoutManager = LinearLayoutManager(this)
                it.isVerticalScrollBarEnabled = true
            })
            .setPositiveButton("RATE IN PLAY STORE") { _, _ -> rateAppOnPlayStore() }
            .show()
    }

    fun supportDev(@Suppress("UNUSED_PARAMETER") view: View) = buyPremium()

    fun gpsExplanation(@Suppress("UNUSED_PARAMETER") view: View) {
        AlertDialog.Builder(this).apply {
            setTitle("Google Play Services")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) setView(R.layout.dialog_gps_explanation) else setView(findViewById<View>(R.id.dialogGPSExplanation))
            show()
        }
    }

    private class InformationAdapter(context: Activity, titleRes: Int, textRes: Int) :
        ListAdapter<Information, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Information>() {

            override fun areItemsTheSame(oldItem: Information, newItem: Information): Boolean = oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Information, newItem: Information): Boolean = oldItem.description == newItem.description
        }) {

        private val layoutInflater = LayoutInflater.from(context)

        init {
            val list = arrayListOf<Information>()
            val titles = context.resources.getStringArray(titleRes)
            val texts = context.resources.getStringArray(textRes)

            for (i in titles.indices)
                list.add(Information(titles[i], texts[i]))

            submitList(list.reversed())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            InformationViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.custom_row_information, parent, false))

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = (holder as InformationViewHolder).bindData(getItem(position))

        private class InformationViewHolder(private val binding: CustomRowInformationBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bindData(data: Information) {
                binding.info = data
                binding.executePendingBindings()
            }
        }
    }

    data class Information(val title: String, val description: String)

    companion object {

        fun openPrivacyPolicy(context: Context) =
            context.startActivity(Intent(Intent.ACTION_VIEW).apply { data = "http://www.nicknackdevelopment.com/privacy-policy.html".toUri() })

        fun giveDevFeedback(context: Context) = context.startActivity(Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("nick@nicknackdevelopment.com"))
            putExtra(Intent.EXTRA_SUBJECT, "DPI Checker Feedback")
            putExtra(Intent.EXTRA_TEXT, "I just wanted to tell you...")
        })

        private fun Context.rateAppOnPlayStore() = try {
            startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=com.nick.mowen.dpichecker".toUri()))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.nick.mowen.dpichecker".toUri()))
        }
    }
}