package com.example.fouroperations.util

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

/**
 * Integração mínima e robusta para compra única "Remover anúncios".
 *
 * Você precisa criar um produto no Google Play Console com esse ID:
 * - PRODUCT_REMOVE_ADS = "remove_ads"
 *
 * Observação: este código NÃO faz validação server-side. Para um app simples, ok.
 * Se isso virar fonte de receita relevante, aí sim vale validar no servidor.
 */
class BillingManager(
    private val appContext: Context,
    private val onAdsRemoved: () -> Unit,
    private val onError: (String) -> Unit = {}
) : PurchasesUpdatedListener {

    companion object {
        const val PRODUCT_REMOVE_ADS = "remove_ads"
    }

    private var billingClient: BillingClient? = null
    private var removeAdsProduct: ProductDetails? = null

    fun start() {
        if (billingClient != null) return

        billingClient = BillingClient.newBuilder(appContext)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        connect()
    }

    fun stop() {
        billingClient?.endConnection()
        billingClient = null
        removeAdsProduct = null
    }

    private fun connect() {
        val client = billingClient ?: return
        if (client.isReady) {
            queryProducts()
            restorePurchases()
            return
        }

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    queryProducts()
                    restorePurchases()
                } else {
                    onError("Billing setup falhou: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Sem retry agressivo aqui. Próxima ação do usuário reativa o fluxo.
            }
        })
    }

    private fun queryProducts() {
        val client = billingClient ?: return
        if (!client.isReady) return

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_REMOVE_ADS)
                        .setProductType(ProductType.INAPP)
                        .build()
                )
            )
            .build()

        client.queryProductDetailsAsync(params) { result, detailsList ->
            if (result.responseCode != BillingResponseCode.OK) {
                onError("Falha ao buscar produtos: ${result.debugMessage}")
                return@queryProductDetailsAsync
            }
            removeAdsProduct = detailsList.firstOrNull()
        }
    }

    fun launchRemoveAdsPurchase(activity: Activity) {
        // Se já está removido, não faz nada
        if (UserPrefs.isAdsRemoved(appContext)) return

        val client = billingClient
        if (client == null) {
            onError("Billing não inicializado.")
            return
        }
        if (!client.isReady) {
            connect()
            onError("Billing ainda conectando. Tente novamente.")
            return
        }

        val product = removeAdsProduct
        if (product == null) {
            queryProducts()
            onError("Produto ainda não carregado. Tente novamente.")
            return
        }

        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product)
            .build()

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()

        val result = client.launchBillingFlow(activity, flowParams)
        if (result.responseCode != BillingResponseCode.OK) {
            onError("Não foi possível iniciar a compra: ${result.debugMessage}")
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                if (purchases.isNullOrEmpty()) return
                handlePurchases(purchases)
            }
            BillingResponseCode.USER_CANCELED -> {
                // Silencioso. Usuário cancelou.
            }
            else -> onError("Compra falhou: ${billingResult.debugMessage}")
        }
    }

    private fun restorePurchases() {
        val client = billingClient ?: return
        if (!client.isReady) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.INAPP)
            .build()

        client.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode != BillingResponseCode.OK) return@queryPurchasesAsync
            handlePurchases(purchases)
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        val client = billingClient ?: return

        val removeAds = purchases.any { purchase ->
            purchase.products.contains(PRODUCT_REMOVE_ADS) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        }

        if (!removeAds) return

        // Acknowledge compras não reconhecidas (obrigatório no Billing)
        purchases
            .filter { it.products.contains(PRODUCT_REMOVE_ADS) }
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            .forEach { purchase ->
                if (!purchase.isAcknowledged) {
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    client.acknowledgePurchase(params) { ackResult ->
                        if (ackResult.responseCode != BillingResponseCode.OK) {
                            onError("Falha ao reconhecer compra: ${ackResult.debugMessage}")
                            return@acknowledgePurchase
                        }
                        unlockRemoveAds()
                    }
                } else {
                    unlockRemoveAds()
                }
            }
    }

    private fun unlockRemoveAds() {
        if (UserPrefs.isAdsRemoved(appContext)) return
        UserPrefs.setAdsRemoved(appContext, true)
        onAdsRemoved()
    }
}
