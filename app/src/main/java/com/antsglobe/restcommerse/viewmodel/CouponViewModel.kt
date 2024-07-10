package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.restcommerse.model.Response.Coupon
import com.antsglobe.restcommerse.model.Response.CouponListResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouponViewModel(private val apiservice: ApiService) : ViewModel() {

    private val _couponlist: MutableLiveData<List<Coupon>?> = MutableLiveData()

    val couponlist: MutableLiveData<List<Coupon>?> get() = _couponlist

    fun getcouponlist(email: String, couponCategory: String) {
        viewModelScope.launch {
            apiservice.couponlist(email,couponCategory).enqueue(object :
                Callback<CouponListResponse> {
                override fun onResponse(
                    call: Call<CouponListResponse>,
                    response: Response<CouponListResponse>
                ) {
                    if (response.isSuccessful) {
                        val content = response.body()?.content
                        _couponlist.value = content
                    }
                }

                override fun onFailure(call: Call<CouponListResponse>, t: Throwable) {
                    Log.d("failed", t.message.toString())
                }

            })
        }
    }

}