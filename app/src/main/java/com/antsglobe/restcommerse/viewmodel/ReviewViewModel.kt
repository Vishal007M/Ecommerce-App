package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.GetReviewResponse
import com.antsglobe.restcommerse.model.Response.ReviewList
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewViewModel(private val apiService: ApiService) : ViewModel() {
    private val _apiReviewListResponse: MutableLiveData<List<ReviewList?>?> = MutableLiveData()
    val getReviewListResponse: MutableLiveData<List<ReviewList?>?> get() = _apiReviewListResponse

    private val _apiGetReviewResponse: MutableLiveData<GetReviewResponse?> = MutableLiveData()
    val getGetReviewResponse: MutableLiveData<GetReviewResponse?> get() = _apiGetReviewResponse


    fun getReviewResponse(product_id: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        try {
            apiService.getReviewList(product_id).enqueue(object :
                Callback<GetReviewResponse> {
                override fun onResponse(
                    call: Call<GetReviewResponse>,
                    response: Response<GetReviewResponse>
                ) {
                    if (response.isSuccessful) {
                        val reviewBody = response.body()
                        _apiGetReviewResponse.value = reviewBody

                        reviewBody?.content?.let {
                            _apiReviewListResponse.value = it
                        }
                    }
                }

                override fun onFailure(call: Call<GetReviewResponse>, t: Throwable) {
                    _apiGetReviewResponse.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}