package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.restcommerse.model.Response.ResetPassResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPassViewModel(private val apiService: ApiService) : ViewModel() {

    private val _apiResponse: MutableLiveData<ResetPassResponse?> = MutableLiveData()
    val apiResponse: MutableLiveData<ResetPassResponse?> get() = _apiResponse

    fun resetPassVM(email: String, pass: String) {
//        if (!NetworkUtils.isNetworkConnected()) {
//            NetworkUtils.showToast()
//            return
//        }
        apiService.resetPassApi(email, pass).enqueue(object : Callback<ResetPassResponse> {
            override fun onResponse(
                call: Call<ResetPassResponse>,
                response: Response<ResetPassResponse>
            ) {
                if (response.isSuccessful) {
                    val resetPassResponse = response.body()
                    _apiResponse.value = resetPassResponse
                    Log.d("LoginViewModel", "Login successful: $resetPassResponse")
                } else {
                    _apiResponse.value = null
                    Log.e(
                        "LoginViewModel",
                        "Login failed: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<ResetPassResponse>, t: Throwable) {
                _apiResponse.value = null
                Log.e("LoginViewModel", "Login error: ${t.message}")
            }
        })
    }
}
