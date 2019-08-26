package com.nick.mowen.dpichecker.skeleton

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.ViewDataBinding
import androidx.preference.PreferenceManager
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

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {

        fun handlePurchase(purchase: Purchase) {
            when (purchase.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> {
                    premium = true

                    if (!purchase.isAcknowledged)
                        acknowledgePurchase(purchase.purchaseToken)
                }
                Purchase.PurchaseState.PENDING ->
                    premium = false
            }
        }

        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null)
            for (purchase in purchases)
                handlePurchase(purchase)
    }

    abstract fun bindViews()

    private fun buildBilling() =
        try {
            billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build()

            billingClient!!.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult?) {
                    if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK)
                        premium = checkPremium()
                    else
                        Toast.makeText(
                            this@AbstractActivity,
                            "Loading of purchases may not be working correctly. If you encounter issues please email me at nick@nicknackdevelopment.com",
                            Toast.LENGTH_LONG
                        ).show()
                }

                override fun onBillingServiceDisconnected() = Unit
            })
        } catch (e: Exception) {
            e.printStackTrace()
            premium = false
        }

    protected fun buyPremium() {
        if (billingClient == null)
            buildBilling()
        else
            billingClient?.querySkuDetailsAsync(SkuDetailsParams.newBuilder().setSkusList(listOf(getString(R.string.soda))).setType(BillingClient.SkuType.INAPP).build()) { _, detailList ->
                if (detailList != null && detailList.isNotEmpty())
                    billingClient?.launchBillingFlow(this, BillingFlowParams.newBuilder().setSkuDetails(detailList[0]).build())
                else
                    Toast.makeText(this, "Please make sure that you are signed in to your Google Play account", Toast.LENGTH_SHORT).show()
            }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        billingClient?.acknowledgePurchase(
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()
        ) { }
    }

    private fun checkPremium(): Boolean {
        val purchaseResults: Purchase.PurchasesResult = billingClient?.queryPurchases(BillingClient.SkuType.INAPP) ?: return false
        val purchases: List<Purchase> = purchaseResults.purchasesList
        return purchases.isNotEmpty()
    }
}