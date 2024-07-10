package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.GetAdminTokenResponse
import com.antsglobe.restcommerse.model.Response.GetOrderDetailsResponse
import com.antsglobe.restcommerse.model.Response.NotificationList
import com.antsglobe.restcommerse.model.Response.NotificationResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetAdminTokenViewModel(private val apiService: ApiService) : ViewModel() {

    private val _getAdminToken: MutableLiveData<GetAdminTokenResponse?> = MutableLiveData()
    val getAdminToken: MutableLiveData<GetAdminTokenResponse?> get() = _getAdminToken

//    fun GetAdminTokenApiVM() = viewModelScope.launch {
////        if (!NetworkUtils.isNetworkConnected()) {
////            NetworkUtils.showToast()
////            return@launch
////        }
//        try {
//            val response = apiService.GetAdminTokenApi().enqueue()
//            if (response.isSuccessful) {
//                val notification = response.body()
//                _getAdminToken.value = notification
//
//
//            }
//        } catch (e: Exception) {
//            println("in catch ${e.message}")
//        }
//    }

    fun GetAdminTokenApiVM() = viewModelScope.launch{
//        if (!NetworkUtils.isNetworkConnected()) {
//            NetworkUtils.showToast()
//            return
//        }
        try {
            apiService.GetAdminTokenApi().enqueue(object :
                Callback<GetAdminTokenResponse> {
                override fun onResponse(
                    call: Call<GetAdminTokenResponse>,
                    response: Response<GetAdminTokenResponse>
                ) {
                    if (response.isSuccessful) {
                        val orderDetailsBody = response.body()
                        _getAdminToken.value = orderDetailsBody

//                        orderDetailsBody?.orderDetails?.let {
//                            _orderDetailsList.value = it
//                        }
                    }
                }

                override fun onFailure(call: Call<GetAdminTokenResponse>, t: Throwable) {
                    _getAdminToken.value = null
                    Log.e("DonePaymentViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}