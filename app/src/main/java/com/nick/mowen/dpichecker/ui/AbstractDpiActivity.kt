package com.nick.mowen.dpichecker.ui

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.billingclient.api.*
import com.nick.mowen.dpichecker.R

abstract class AbstractDpiActivity : AppCompatActivity(), PurchasesUpdatedListener {

    protected var ads: Boolean = false
    private var billingClient: BillingClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildBilling()
        ads = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("ads", true)
    }

    override fun onStop() {
        PreferenceManager.getDefaultSharedPreferences(this).edit { putBoolean("ads", !checkPremium()) }
        super.onStop()
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            ads = false
        }
    }

    protected fun buildBilling() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .build()

        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(responseCode: Int) {
                if (responseCode == BillingClient.BillingResponse.OK)
                    ads = !checkPremium()
            }

            override fun onBillingServiceDisconnected() {

            }
        })
    }

    protected fun buyPremium() {
        if (billingClient == null)
            buildBilling()
        else {
            val builder: BillingFlowParams.Builder = BillingFlowParams.newBuilder()
                    .setSku(getString(R.string.soda))
                    .setType(BillingClient.SkuType.INAPP)
            billingClient!!.launchBillingFlow(this, builder.build())
        }
    }

    protected abstract fun bindViews()

    protected abstract fun updateAds()

    private fun checkPremium(): Boolean {
        val purchaseResults: Purchase.PurchasesResult = billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
        val purchases: List<Purchase> = purchaseResults.purchasesList
        return purchases.isNotEmpty()
    }
}