package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.GetOrderTaxResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaxAmountViewModel(private val apiService: ApiService) : ViewModel() {

    private val _apiTaxResponse: MutableLiveData<GetOrderTaxResponse?> = MutableLiveData()
    val getTaxResponse: MutableLiveData<GetOrderTaxResponse?> get() = _apiTaxResponse

    fun TaxResponseVM(
        email: String,
        total_price: String?,
        promodisc: String?,
        shipcharge: String?,
        other_disc: String?,
    ) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }

        try {
            apiService.getOrderTaxCalApi(email, total_price, promodisc, shipcharge, other_disc)
                .enqueue(object : Callback<GetOrderTaxResponse> {
                    override fun onResponse(
                        call: Call<GetOrderTaxResponse>, response: Response<GetOrderTaxResponse>
                    ) {
                        if (response.isSuccessful) {
                            val profileBody = response.body()
                            _apiTaxResponse?.value = profileBody
                        }
                    }

                    override fun onFailure(call: Call<GetOrderTaxResponse>, t: Throwable) {
                        Log.d("taxCal", t.toString())
                    }
                })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }


}