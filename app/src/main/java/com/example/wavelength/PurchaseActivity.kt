package com.example.wavelength

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.android.billingclient.api.Purchase.PurchaseState
import com.example.wavelength.R.id
import com.example.wavelength.R.layout
import java.io.IOException
import java.util.*

class PurchaseActivity : AppCompatActivity(), PurchasesUpdatedListener {

    var purchaseStatus: TextView? = null
    var purchaseButton: Button? = null
    private var billingClient: BillingClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_purchase)
        purchaseStatus = findViewById<View>(id.purchase_status) as TextView
        purchaseButton = findViewById<View>(id.purchase_button) as Button
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases().setListener(this).build()
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    val queryPurchase = billingClient!!.queryPurchases(INAPP)
                    val queryPurchases: List<Purchase>? = queryPurchase.purchasesList
                    if (queryPurchases != null && queryPurchases.size > 0) {
                        handlePurchases(queryPurchases)
                    }
//if purchase list is empty that means item is not purchased
//Or purchase is refunded or canceled
                    else{
                        savePurchaseValueToPref(false);
                    }
                }
            }
            override fun onBillingServiceDisconnected() {}
        })
//item Purchased
        if (purchaseValueFromPref) {
            purchaseButton!!.visibility = View.GONE
            purchaseStatus!!.text = "Purchase Status : Purchased"
        }
//item not Purchased
        else {
            purchaseButton!!.visibility = View.VISIBLE
            purchaseStatus!!.text = "Purchase Status : Not Purchased"
        }
    }
    private val preferenceObject: SharedPreferences
        get() = applicationContext.getSharedPreferences(PREF_FILE, 0)
    private val preferenceEditObject: Editor
        get() {
            val pref: SharedPreferences = applicationContext.getSharedPreferences(PREF_FILE, 0)
            return pref.edit()
        }
    private val purchaseValueFromPref: Boolean
        get() = preferenceObject.getBoolean(PURCHASE_KEY, false)
    private fun savePurchaseValueToPref(value: Boolean) {
        preferenceEditObject.putBoolean(PURCHASE_KEY, value).commit()
    }
    //initiate purchase on button click
    fun purchase(view: View?) {
//check if service is already connected
        if (billingClient!!.isReady) {
            initiatePurchase()
        }
//else reconnect service
        else {
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        initiatePurchase()
                    } else {
                        Toast.makeText(applicationContext, "Error " + billingResult.debugMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onBillingServiceDisconnected() {}
            })
        }
    }
    private fun initiatePurchase() {
        val skuList: MutableList<String> = ArrayList()
        skuList.add(PRODUCT_ID)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(INAPP)
        billingClient!!.querySkuDetailsAsync(params.build())
        { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                if (skuDetailsList != null && skuDetailsList.size > 0) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList[0])
                        .build()
                    billingClient!!.launchBillingFlow(this@PurchaseActivity, flowParams)
                } else {
//try to add item/product id "purchase" inside managed product in google play console
                    Toast.makeText(applicationContext, "Purchase Item not Found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext,
                    " Error " + billingResult.debugMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
//if item newly purchased
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        }
//if item already purchased then check and reflect changes
        else if (billingResult.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {
            val queryAlreadyPurchasesResult = billingClient!!.queryPurchases(INAPP)
            val alreadyPurchases: List<Purchase>? = queryAlreadyPurchasesResult.purchasesList
            if (alreadyPurchases != null) {
                handlePurchases(alreadyPurchases)
            }
        }
//if purchase cancelled
        else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            Toast.makeText(applicationContext, "Purchase Canceled", Toast.LENGTH_SHORT).show()
        }
// Handle any other error msgs
        else {
            Toast.makeText(applicationContext, "Error " + billingResult.debugMessage, Toast.LENGTH_SHORT).show()
        }
    }
    fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
//if item is purchased
            if (PRODUCT_ID == purchase.sku && purchase.purchaseState == PurchaseState.PURCHASED) {
                if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
// Invalid purchase
// show error to user
                    Toast.makeText(applicationContext, "Error : Invalid Purchase", Toast.LENGTH_SHORT).show()
                    return
                }
// else purchase is valid
//if item is purchased and not acknowledged
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient!!.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase)
                }
//else item is purchased and also acknowledged
                else {
// Grant entitlement to the user on item purchase
// restart activity
                    if (!purchaseValueFromPref) {
                        savePurchaseValueToPref(true)
                        Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT).show()
                        recreate()
                    }
                }
            }
//if purchase is pending
            else if (PRODUCT_ID == purchase.sku && purchase.purchaseState == PurchaseState.PENDING) {
                Toast.makeText(applicationContext,
                    "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show()
            }
//if purchase is refunded or unknown
            else if (PRODUCT_ID == purchase.sku && purchase.purchaseState == PurchaseState.UNSPECIFIED_STATE) {
                savePurchaseValueToPref(false)
                purchaseStatus!!.text = "Purchase Status : Not Purchased"
                purchaseButton!!.visibility = View.VISIBLE
                Toast.makeText(applicationContext, "Purchase Status Unknown", Toast.LENGTH_SHORT).show()
            }
        }
    }
    var ackPurchase = AcknowledgePurchaseResponseListener { billingResult ->
        if (billingResult.responseCode == BillingResponseCode.OK) {
//if purchase is acknowledged
// Grant entitlement to the user. and restart activity
            savePurchaseValueToPref(true)
            Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT).show()
            recreate()
        }
    }
    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     *
     * Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     *
     */
    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
// To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            val base64Key = "Add Your Key Here"
            Security.verifyPurchase(base64Key, signedData, signature)
        } catch (e: IOException) {
            false
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (billingClient != null) {
            billingClient!!.endConnection()
        }
    }
    companion object {
        const val PREF_FILE = "MyPref"
        const val PURCHASE_KEY = "purchase"
        const val PRODUCT_ID = "purchase"
    }
}