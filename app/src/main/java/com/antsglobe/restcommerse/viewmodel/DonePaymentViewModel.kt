package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.GetOrderDetailsResponse
import com.antsglobe.restcommerse.model.Response.GetRepeatOrderResponse
import com.antsglobe.restcommerse.model.Response.OrderDetail
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DonePaymentViewModel(private val apiservice: ApiService) : ViewModel() {

    private val _orderDetailsList: MutableLiveData<List<OrderDetail?>?> = MutableLiveData()
    val orderDetailsList: MutableLiveData<List<OrderDetail?>?> get() = _orderDetailsList

    private val _apiGetOrderDetailsResponse: MutableLiveData<GetOrderDetailsResponse?> =
        MutableLiveData()
    val getGetOrderDetailsResponse: MutableLiveData<GetOrderDetailsResponse?> get() = _apiGetOrderDetailsResponse

    private val _apiGetOrderRepeatResponse: MutableLiveData<GetRepeatOrderResponse?> =
        MutableLiveData()
    val getOrderRepeatResponse: MutableLiveData<GetRepeatOrderResponse?> get() = _apiGetOrderRepeatResponse


    fun getOrderDetailsResponse(email: String, order_id: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        try {
            apiservice.getOrderDetailsApi(email, order_id).enqueue(object :
                Callback<GetOrderDetailsResponse> {
                override fun onResponse(
                    call: Call<GetOrderDetailsResponse>,
                    response: Response<GetOrderDetailsResponse>
                ) {
                    if (response.isSuccessful) {
                        val orderDetailsBody = response.body()
                        _apiGetOrderDetailsResponse.value = orderDetailsBody

                        orderDetailsBody?.orderDetails?.let {
                            _orderDetailsList.value = it
                        }
                    }
                }

                override fun onFailure(call: Call<GetOrderDetailsResponse>, t: Throwable) {
                    _apiGetOrderDetailsResponse.value = null
                    Log.e("DonePaymentViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

    fun getOrderRepeatResponse(email: String, order_id: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        try {
            apiservice.getOrderRepeatApi(email, order_id).enqueue(object :
                Callback<GetRepeatOrderResponse> {
                override fun onResponse(
                    call: Call<GetRepeatOrderResponse>,
                    response: Response<GetRepeatOrderResponse>
                ) {
                    if (response.isSuccessful) {
                        val orderDetailsBody = response.body()
                        _apiGetOrderRepeatResponse.value = orderDetailsBody

                    }
                }

                override fun onFailure(call: Call<GetRepeatOrderResponse>, t: Throwable) {
                    _apiGetOrderRepeatResponse.value = null
                    Log.e("DonePaymentViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }
}