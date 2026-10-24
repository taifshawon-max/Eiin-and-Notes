package com.example.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.example.data.model.PricingCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Official Google Play Billing Client integration (Google Play In-App Purchases ONLY).
 * Strictly prohibits external payment gateways in compliance with Google Play Developer Policy.
 */
class GooglePlayBillingHelper(
    context: Context,
    private val onPurchaseVerified: (sku: String, purchaseToken: String, orderId: String) -> Unit,
    private val onBillingStatusMessage: (String) -> Unit
) : PurchasesUpdatedListener {

    private val _isPlayStoreConnected = MutableStateFlow(false)
    val isPlayStoreConnected: StateFlow<Boolean> = _isPlayStoreConnected.asStateFlow()

    private val productDetailsMap = mutableMapOf<String, ProductDetails>()

    private val billingClient: BillingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    init {
        startPlayBillingConnection()
    }

    fun startPlayBillingConnection() {
        if (billingClient.isReady) {
            _isPlayStoreConnected.value = true
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isPlayStoreConnected.value = true
                    queryAllInAppProducts()
                } else {
                    _isPlayStoreConnected.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                _isPlayStoreConnected.value = false
            }
        })
    }

    private fun queryAllInAppProducts() {
        val allSkus = buildList {
            addAll(PricingCatalog.schoolPackages.map { it.sku })
            addAll(PricingCatalog.notePackages.map { it.sku })
            addAll(PricingCatalog.qaPackages.map { it.sku })
        }

        val productList = allSkus.map { sku ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(sku)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList.forEach { details ->
                    productDetailsMap[details.productId] = details
                }
            }
        }
    }

    /**
     * Attempts to launch the native Google Play Store In-App Purchase flow if the Play Store
     * service is connected and the SKU is configured in Google Play Console.
     * Returns true if native Play Store sheet was launched, or false to fall back to the
     * in-app Google Play Billing Sandbox sheet for testing in the emulator.
     */
    fun launchGooglePlayFlowIfConfigured(activity: Activity?, sku: String): Boolean {
        if (activity == null || !billingClient.isReady) return false
        val details = productDetailsMap[sku] ?: return false

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        return result.responseCode == BillingClient.BillingResponseCode.OK
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    handlePlayPurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                onBillingStatusMessage("Google Play Billing: Purchase cancelled by user.")
            }
            else -> {
                onBillingStatusMessage("Google Play Billing Error (${billingResult.debugMessage})")
            }
        }
    }

    private fun handlePlayPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.consumeAsync(consumeParams) { billingResult, _ ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val sku = purchase.products.firstOrNull() ?: "unknown_sku"
                    val orderId = purchase.orderId ?: "GPA.${System.currentTimeMillis()}"
                    onPurchaseVerified(sku, purchase.purchaseToken, orderId)
                }
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            onBillingStatusMessage("Google Play Billing: Purchase is pending confirmation.")
        }
    }

    fun restoreGooglePlayPurchases() {
        if (!billingClient.isReady) return
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchasesList.forEach { handlePlayPurchase(it) }
            }
        }
    }
}
