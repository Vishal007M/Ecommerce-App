package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.restcommerse.model.Response.OrderListResponse
import com.antsglobe.restcommerse.model.Response.OrderResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyOrderViewModel(private val apiService: ApiService) : ViewModel() {

    private val _orderresponse: MutableLiveData<List<OrderResponse>> = MutableLiveData()

    val orderresponse: MutableLiveData<List<OrderResponse>> get() = _orderresponse


    fun getorderlist(email: String) {
        viewModelScope.launch {
            apiService.orderlist(email).enqueue(object : Callback<OrderListResponse> {
                override fun onResponse(
                    call: Call<OrderListResponse>,
                    response: Response<OrderListResponse>
                ) {
                    if (response.isSuccessful) {
                        _orderresponse.value = response.body()?.content!!
                    }
                }

                override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                    Log.d("failed", t.message.toString())
                }

            })
        }
    }


}