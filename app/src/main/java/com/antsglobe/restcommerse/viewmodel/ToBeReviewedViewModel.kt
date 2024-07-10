package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.GetTobeReviewedListResponse
import com.antsglobe.restcommerse.model.Response.TobeReviewedList
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToBeReviewedViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getToBeReview: MutableLiveData<GetTobeReviewedListResponse?> = MutableLiveData()
    val getToBeReview: MutableLiveData<GetTobeReviewedListResponse?> get() = _getToBeReview

    private val _getToBeReviewItem: MutableLiveData<List<TobeReviewedList>> = MutableLiveData()
    val getToBeReviewItem: LiveData<List<TobeReviewedList>> get() = _getToBeReviewItem

    fun getTobeReviewedListVM(
        email: String,
    ) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            val response = apiService.getTobeReviewedListApi(email)
                .enqueue(object : Callback<GetTobeReviewedListResponse> {
                    override fun onResponse(
                        call: Call<GetTobeReviewedListResponse>,
                        response: Response<GetTobeReviewedListResponse>
                    ) {
                        if (response.isSuccessful) {
                            val notification = response.body()
                            _getToBeReview.value = notification

                            notification?.content?.let { content ->
                                _getToBeReviewItem.value = content
                            }

                        }
                    }

                    override fun onFailure(call: Call<GetTobeReviewedListResponse>, t: Throwable) {
                        _getToBeReview.value = null
                        Log.e("LoginViewModel", "Login error: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}