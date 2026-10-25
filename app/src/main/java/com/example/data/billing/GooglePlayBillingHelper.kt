package com.example.data.billing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
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
 * Official Google Play Billing Client v7 integration (Google Play In-App Purchases ONLY).
 * Never auto-grants credits—credits are ONLY granted when Google Play Store completes and
 * verifies a real purchase via PurchasesUpdatedListener and consumeAsync.
 */
class GooglePlayBillingHelper(
    private val context: Context,
    private val onPurchaseVerified: (sku: String, purchaseToken: String, orderId: String) -> Unit,
    private val onBillingStatusMessage: (String) -> Unit,
    private val onBillingDiagnosticRequired: (String) -> Unit = {}
) : PurchasesUpdatedListener {

    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isPlayStoreConnected = MutableStateFlow(false)
    val isPlayStoreConnected: StateFlow<Boolean> = _isPlayStoreConnected.asStateFlow()

    private val _isLaunchingBillingFlow = MutableStateFlow(false)
    val isLaunchingBillingFlow: StateFlow<Boolean> = _isLaunchingBillingFlow.asStateFlow()

    private val _lastBillingDiagnostic = MutableStateFlow<String?>(null)
    val lastBillingDiagnostic: StateFlow<String?> = _lastBillingDiagnostic.asStateFlow()

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

    fun startPlayBillingConnection(onConnected: (() -> Unit)? = null) {
        if (billingClient.isReady) {
            _isPlayStoreConnected.value = true
            onConnected?.invoke()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isPlayStoreConnected.value = true
                    _lastBillingDiagnostic.value = null
                    preloadCatalogProductDetails()
                    onConnected?.invoke()
                } else {
                    _isPlayStoreConnected.value = false
                    val reason = formatResponseCode(billingResult.responseCode, billingResult.debugMessage)
                    _lastBillingDiagnostic.value =
                        "Google Play Store Billing service could not connect ($reason). Please ensure the Google Play Store app is installed, updated, and signed into a Google account on this phone."
                }
            }

            override fun onBillingServiceDisconnected() {
                _isPlayStoreConnected.value = false
            }
        })
    }

    private fun preloadCatalogProductDetails() {
        val allSkus = buildList {
            addAll(PricingCatalog.schoolPackages.map { it.sku })
            addAll(PricingCatalog.notePackages.map { it.sku })
            addAll(PricingCatalog.qaPackages.map { it.sku })
        }

        // Query in batches of 20 so Google Play Billing processes each catalog group reliably
        allSkus.chunked(20).forEach { batch ->
            val productList = batch.map { sku ->
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
    }

    /**
     * Connects to Google Play Billing, queries the ProductDetails for [sku], and launches the
     * genuine Google Play Store payment sheet (`billingClient.launchBillingFlow`).
     * Never grants credits automatically.
     */
    fun launchGooglePlayBillingFlow(activity: Activity?, sku: String) {
        if (activity == null) {
            val msg = "Unable to open Google Play Billing sheet: Activity context is required."
            _lastBillingDiagnostic.value = msg
            onBillingStatusMessage(msg)
            onBillingDiagnosticRequired(msg)
            return
        }

        _isLaunchingBillingFlow.value = true
        onBillingStatusMessage("Connecting to Google Play Store for $sku...")

        if (!billingClient.isReady) {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _isPlayStoreConnected.value = true
                        queryAndLaunchSingleSku(activity, sku)
                    } else {
                        _isPlayStoreConnected.value = false
                        _isLaunchingBillingFlow.value = false
                        val reason = formatResponseCode(billingResult.responseCode, billingResult.debugMessage)
                        val msg = "Google Play Store Billing is unavailable on this device ($reason). Make sure you are signed into Google Play Store."
                        _lastBillingDiagnostic.value = msg
                        onBillingDiagnosticRequired(msg)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    _isPlayStoreConnected.value = false
                    _isLaunchingBillingFlow.value = false
                }
            })
        } else {
            queryAndLaunchSingleSku(activity, sku)
        }
    }

    private fun queryAndLaunchSingleSku(activity: Activity, sku: String) {
        val cachedDetails = productDetailsMap[sku]
        if (cachedDetails != null) {
            launchBillingSheetOnMainThread(activity, sku, cachedDetails)
            return
        }

        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(sku)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            mainHandler.post {
                _isLaunchingBillingFlow.value = false
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                    val details = productDetailsList.first()
                    productDetailsMap[sku] = details
                    launchBillingSheetOnMainThread(activity, sku, details)
                } else {
                    val codeDesc = formatResponseCode(billingResult.responseCode, billingResult.debugMessage)
                    val msg = buildString {
                        append("Google Play Store connected ($codeDesc), but Product ID '$sku' was not returned by Google Play Console for package '${context.packageName}'. ")
                        append("To complete real Google Play payments on your phone, upload this signed app bundle to Google Play Console (Internal/Closed Testing) and activate In-App Product ID '$sku' under Monetize → Products → In-app products. ")
                        append("No automatic credits were added.")
                    }
                    _lastBillingDiagnostic.value = msg
                    onBillingDiagnosticRequired(msg)
                }
            }
        }
    }

    private fun launchBillingSheetOnMainThread(
        activity: Activity,
        sku: String,
        details: ProductDetails
    ) {
        mainHandler.post {
            _isLaunchingBillingFlow.value = false
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .build()
            )

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val result = billingClient.launchBillingFlow(activity, billingFlowParams)
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                val reason = formatResponseCode(result.responseCode, result.debugMessage)
                val msg = "Google Play launchBillingFlow returned $reason for Product ID '$sku'. No credits were added."
                _lastBillingDiagnostic.value = msg
                onBillingDiagnosticRequired(msg)
            }
        }
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
                onBillingStatusMessage("Google Play payment cancelled by user. No credits added.")
            }
            else -> {
                val reason = formatResponseCode(billingResult.responseCode, billingResult.debugMessage)
                onBillingStatusMessage("Google Play Billing: $reason")
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
                } else {
                    val reason = formatResponseCode(billingResult.responseCode, billingResult.debugMessage)
                    onBillingStatusMessage("Google Play purchase verification error: $reason")
                }
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            onBillingStatusMessage("Google Play Billing: Payment is pending confirmation in Google Play Store.")
        }
    }

    fun restoreGooglePlayPurchases() {
        if (!billingClient.isReady) {
            startPlayBillingConnection {
                queryExistingPlayPurchases()
            }
            return
        }
        queryExistingPlayPurchases()
    }

    private fun queryExistingPlayPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchasesList.isEmpty()) {
                    onBillingStatusMessage("Google Play Store checked: No unconsumed in-app purchases found.")
                } else {
                    purchasesList.forEach { handlePlayPurchase(it) }
                }
            } else {
                val reason = formatResponseCode(billingResult.responseCode, billingResult.debugMessage)
                onBillingStatusMessage("Google Play Restore failed: $reason")
            }
        }
    }

    fun openGooglePlayStoreListing() {
        try {
            val marketIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (_: Exception) {
            try {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (_: Exception) {
                onBillingStatusMessage("Unable to open Google Play Store app.")
            }
        }
    }

    private fun formatResponseCode(code: Int, debugMessage: String?): String {
        val codeLabel = when (code) {
            BillingClient.BillingResponseCode.OK -> "OK"
            BillingClient.BillingResponseCode.USER_CANCELED -> "USER_CANCELED"
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE"
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "DEVELOPER_ERROR"
            BillingClient.BillingResponseCode.ERROR -> "ERROR"
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
            BillingClient.BillingResponseCode.NETWORK_ERROR -> "NETWORK_ERROR"
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "SERVICE_DISCONNECTED"
            else -> "CODE_$code"
        }
        return if (!debugMessage.isNullOrBlank()) "$codeLabel: $debugMessage" else codeLabel
    }
}
