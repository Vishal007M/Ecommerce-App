package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.restcommerse.model.Response.LoginResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val apiService: ApiService) : ViewModel() {

    private val _apiResponse: MutableLiveData<List<LoginResponse>?> = MutableLiveData()
    val apiResponse: MutableLiveData<List<LoginResponse>?> get() = _apiResponse

    fun login(email: String, password: String, rem: String) {
//        if (!NetworkUtils.isNetworkConnected()) {
//            NetworkUtils.showToast()
//            return
//        }
        apiService.login(email, password, rem).enqueue(object : Callback<List<LoginResponse>> {
            override fun onResponse(
                call: Call<List<LoginResponse>>,
                response: Response<List<LoginResponse>>
            ) {
                if (response.isSuccessful) {
                    val loginResponseList = response.body()
                    _apiResponse.value = loginResponseList
                    Log.d("LoginViewModel", "Login successful: $loginResponseList")
                } else {
                    _apiResponse.value = null
                    Log.e(
                        "LoginViewModel",
                        "Login failed: ${response.code()} - ${response.message()}"
                    )

                }
            }

            override fun onFailure(call: Call<List<LoginResponse>>, t: Throwable) {
                _apiResponse.value = null
                Log.e("LoginViewModel", "Login error: ${t.message}")
            }
        })
    }
}
