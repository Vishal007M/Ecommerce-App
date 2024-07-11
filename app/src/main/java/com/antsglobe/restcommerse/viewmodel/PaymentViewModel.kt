package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.BuySingleProductResponse
import com.antsglobe.restcommerse.model.Response.GetPaymentApi
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentViewModel(private val apiService: ApiService) : ViewModel() {

    private val _getPaymentResponse: MutableLiveData<GetPaymentApi?> = MutableLiveData()
    val getPaymentResponse: MutableLiveData<GetPaymentApi?> get() = _getPaymentResponse

    private val _getSinglePaymentResponse: MutableLiveData<BuySingleProductResponse?> =
        MutableLiveData()
    val getSinglePaymentResponse: MutableLiveData<BuySingleProductResponse?> get() = _getSinglePaymentResponse

    fun GetPaymentResponse(
        email: String,
        transactionId: String,
        totalPrice: String,
        paymentMethod: String,
        paymentStatus: String,
        addressId: String,
        discountAmount: String,
        promoCode: String,
        promodisc: String,
        shipCharge: String,
        otherDisc: String,
        taxPer: String,
        taxAmount: String,
        grandTotal: String
    ) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        apiService.getPaymentApi(
            email, transactionId, totalPrice, paymentMethod, paymentStatus,
            addressId, discountAmount, promoCode, promodisc, shipCharge, otherDisc,
            taxPer, taxAmount, grandTotal

        ).enqueue(object :
            Callback<GetPaymentApi> {
            override fun onResponse(
                call: Call<GetPaymentApi>,
                response: Response<GetPaymentApi>
            ) {
                if (response.isSuccessful) {
                    val getPaymentResponse = response.body()
                    _getPaymentResponse.value = getPaymentResponse
                    Log.d("getPayment", "Get Payment successful: ${getPaymentResponse}")
                } else {
                    _getPaymentResponse.value = null
                    Log.e(
                        "getPayment",
                        "Get Payment failed: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<GetPaymentApi>, t: Throwable) {
                _getPaymentResponse.value = null
                Log.e("PaymentViewModel", "Get Payment error: ${t.message}")
            }
        })
    }

    fun GetSinglePaymentResponse(
        email: String,
        transactionId: String,
        totalPrice: String,
        paymentMethod: String,
        paymentStatus: String,
        addressId: String,
        discountAmount: String,
        promoCode: String,
        promodisc: String,
        shipCharge: String,
        otherDisc: String,
        productId: String,
        quantity: String,
        price: String,
        discountPrice: String,
        variationId: String,
        grandTotal: String,
        taxPer: String,
        taxamt: String

    ) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        apiService.getSinglePaymentApi(
            email, transactionId, totalPrice, paymentMethod, paymentStatus,
            addressId, discountAmount, promoCode, promodisc, shipCharge, otherDisc, productId,
            quantity, price, discountPrice, variationId, grandTotal, taxPer, taxamt
        ).enqueue(object :
            Callback<BuySingleProductResponse> {
            override fun onResponse(
                call: Call<BuySingleProductResponse>,
                response: Response<BuySingleProductResponse>
            ) {
                if (response.isSuccessful) {
                    val getPaymentResponse = response.body()
                    getSinglePaymentResponse.value = getPaymentResponse
                    Log.d("getPayment", "Get Payment successful: ${getPaymentResponse}")
                } else {
                    getSinglePaymentResponse.value = null
                    Log.e(
                        "getPayment",
                        "Get Payment failed: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<BuySingleProductResponse>, t: Throwable) {
                _getPaymentResponse.value = null
                Log.e("PaymentViewModel", "Get Payment error: ${t.message}")
            }
        })
    }
}