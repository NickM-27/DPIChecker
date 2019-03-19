package com.nick.mowen.dpichecker.skeleton

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.ViewDataBinding
import com.android.billingclient.api.*
import com.nick.mowen.dpichecker.R

abstract class AbstractActivity : AppCompatActivity(), PurchasesUpdatedListener {

    protected abstract val binding: ViewDataBinding
    private var billingClient: BillingClient? = null
    protected var premium = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildBilling()
    }

    override fun onStop() {
        PreferenceManager.getDefaultSharedPreferences(this).edit { putBoolean("ads", !checkPremium()) }
        super.onStop()
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) = Unit

    abstract fun bindViews()

    private fun buildBilling() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .build()

        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(responseCode: Int) {
                if (responseCode == BillingClient.BillingResponse.OK)
                    premium = !checkPremium()
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

    private fun checkPremium(): Boolean {
        val purchaseResults: Purchase.PurchasesResult = billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
        val purchases: List<Purchase> = purchaseResults.purchasesList
        return purchases.isNotEmpty()
    }
}