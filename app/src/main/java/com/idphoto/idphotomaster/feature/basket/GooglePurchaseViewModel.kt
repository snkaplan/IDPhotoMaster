package com.idphoto.idphotomaster.feature.basket

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.consumePurchase
import com.google.common.collect.ImmutableList
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.Constants
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GooglePurchaseViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel<GooglePurchaseViewState>() {

    private val purchaseUpdateListener = PurchasesUpdatedListener { result, purchases ->
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                updateState { copy(purchaseSuccess = triggered) }
                handlePurchase(purchase)
            }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // User canceled the purchase
            updateState { copy(userCancelledPurchase = triggered) }
        } else {
            // Handle other error cases
            updateState { copy(purchaseFailed = triggered) }
        }
    }

    private lateinit var billingClient: BillingClient

    fun onTriggerViewEvent(event: GooglePurchaseViewEvent) {
        viewModelScope.launch {
            when (event) {
                is GooglePurchaseViewEvent.OnCompletePurchase -> onCompletePurchase(event.productId, event.activity)
                GooglePurchaseViewEvent.OnNavigateLoginConsumed -> updateState { copy(navigateToLogin = consumed) }
                GooglePurchaseViewEvent.OnPurchaseSuccessConsumed -> updateState { copy(purchaseSuccess = consumed) }
                GooglePurchaseViewEvent.OnPurchaseFailedConsumed -> updateState { copy(purchaseFailed = consumed) }
                GooglePurchaseViewEvent.OnUserCancelledPurchaseConsumed -> updateState { copy(userCancelledPurchase = consumed) }
            }
        }
    }

    private fun onCompletePurchase(productId: String, activity: Activity) {
        if (userRepository.currentUser == null) {
            updateState { copy(navigateToLogin = triggered) }
        } else {
            purchase(productId, activity)
        }
    }


    fun billingSetup(context: Context) {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchaseUpdateListener)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    println("Purchase: billing setup completed")
                    getPrice()
                }
            }

            override fun onBillingServiceDisconnected() {
                println("Purchase: billing setup disconnected")
            }
        })
    }

    private fun handlePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val listener = ConsumeResponseListener { _, _ -> }
        billingClient.consumeAsync(consumeParams, listener)
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            viewModelScope.launch {
                billingClient.consumePurchase(consumeParams)
            }
        }
    }

    private fun purchase(productId: String, activity: Activity) {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                ).build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetails = productDetailsList.firstOrNull { productDetails ->
                    productDetails.productId == productId
                }
                productDetails?.let {
                    val productDetailsParamsList = listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(it)
                            .build()
                    )
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()
                    billingClient.launchBillingFlow(activity, billingFlowParams)
                }
            }
        }
    }

    fun checkProducts() {
        val queryPurchaseParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(
            queryPurchaseParams
        ) { result, purchases ->
            when (result.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    for (purchase in purchases) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            return@queryPurchasesAsync
                        }
                    }
                }

                BillingClient.BillingResponseCode.USER_CANCELED -> {}

                else -> {}
            }
        }
    }

    private fun getPrice() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(Constants.PRODUCT_ID)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                ).build()
        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            println("Billing-> ${billingResult.responseCode}")
            println("Billing-> ${productDetailsList}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetails = productDetailsList.firstOrNull { productDetails ->
                    productDetails.productId == Constants.PRODUCT_ID
                }
                productDetails?.let {
                    updateState { copy(photoPrice = it.oneTimePurchaseOfferDetails?.formattedPrice) }
                }
            }
        }
    }

    override fun createInitialState(): GooglePurchaseViewState {
        return GooglePurchaseViewState()
    }
}

data class GooglePurchaseViewState(
    val purchaseSuccess: StateEvent = consumed,
    val purchaseFailed: StateEvent = consumed,
    val userCancelledPurchase: StateEvent = consumed,
    val navigateToLogin: StateEvent = consumed,
    val photoPrice: String? = null
) : IViewState

sealed interface GooglePurchaseViewEvent {
    data class OnCompletePurchase(
        val productId: String,
        val activity: Activity
    ) : GooglePurchaseViewEvent

    data object OnPurchaseSuccessConsumed : GooglePurchaseViewEvent
    data object OnPurchaseFailedConsumed : GooglePurchaseViewEvent
    data object OnUserCancelledPurchaseConsumed : GooglePurchaseViewEvent
    data object OnNavigateLoginConsumed : GooglePurchaseViewEvent
}