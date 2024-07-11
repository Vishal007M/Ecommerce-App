package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.AddReviewResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddReviewViewModel(private val apiService: ApiService) : ViewModel() {

    private val _addReviewsResponse: MutableLiveData<AddReviewResponse?> = MutableLiveData()
    val addReviewsResponse: MutableLiveData<AddReviewResponse?> get() = _addReviewsResponse

    fun AddReviewResponse(
        email: String,
        productId: String,
        review: String,
        rating: String,
        img: String,
        img_url: String
    ) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        apiService.addCustomerReviews(email, productId, review, rating, img, img_url)
            .enqueue(object :
                Callback<AddReviewResponse> {
                override fun onResponse(
                    call: Call<AddReviewResponse>,
                    response: Response<AddReviewResponse>
                ) {
                    if (response.isSuccessful) {
                        val addReviewResponse = response.body()
                        _addReviewsResponse.value = addReviewResponse
                        Log.d("addReview", "Add review successful: ${addReviewResponse}")
                    } else {
                        _addReviewsResponse.value = null
                        Log.e(
                            "addReview",
                            "Add review  failed: ${response.code()} - ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) {
                    _addReviewsResponse.value = null
                    Log.e("ReviewViewModel", "Add review error: ${t.message}")
                }
            })
    }
}