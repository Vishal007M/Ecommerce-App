package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.restcommerse.model.Response.OtpResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpViewModel(private val apiService: ApiService) : ViewModel() {

    private val _apiResponse: MutableLiveData<OtpResponse?> = MutableLiveData()
    val apiResponse: MutableLiveData<OtpResponse?> get() = _apiResponse

    fun OtpVM(name: String, email: String) {
//        if (!NetworkUtils.isNetworkConnected()) {
//            NetworkUtils.showToast()
//            return
//        }
        apiService.otpApi(name, email).enqueue(object : Callback<OtpResponse> {
            override fun onResponse(
                call: Call<OtpResponse>,
                response: Response<OtpResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    _apiResponse.value = loginResponse
                    Log.d("LoginViewModel", "Login successful: $loginResponse")
                } else {
                    _apiResponse.value = null
                    Log.e(
                        "LoginViewModel",
                        "Login failed: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                _apiResponse.value = null
                Log.e("LoginViewModel", "Login error: ${t.message}")
            }
        })
    }
}
