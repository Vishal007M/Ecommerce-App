package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.GetReviewedListResponse
import com.antsglobe.restcommerse.model.Response.ReviewedList
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewedViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getReview: MutableLiveData<GetReviewedListResponse?> = MutableLiveData()
    val getReview: MutableLiveData<GetReviewedListResponse?> get() = _getReview

    private val _getReviewItem: MutableLiveData<List<ReviewedList>> = MutableLiveData()
    val getReviewItem: LiveData<List<ReviewedList>> get() = _getReviewItem

    fun getReviewedListVM(
        email: String,
    ) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            val response = apiService.getReviewedListApi(email)
                .enqueue(object : Callback<GetReviewedListResponse> {
                    override fun onResponse(
                        call: Call<GetReviewedListResponse>,
                        response: Response<GetReviewedListResponse>
                    ) {
                        if (response.isSuccessful) {
                            val notification = response.body()
                            _getReview.value = notification

                            notification?.content?.let { content ->
                                _getReviewItem.value = content
                            }

                        }
                    }

                    override fun onFailure(call: Call<GetReviewedListResponse>, t: Throwable) {
                        _getReview.value = null
                        Log.e("LoginViewModel", "Login error: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}