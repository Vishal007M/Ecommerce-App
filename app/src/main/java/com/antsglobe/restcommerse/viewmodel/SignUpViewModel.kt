package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.restcommerse.model.Request.SignUpRequest
import com.antsglobe.restcommerse.model.Response.OtpResponse
import com.antsglobe.restcommerse.model.Response.SignUpResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel(private val apiService: ApiService) : ViewModel() {

    private val _apiResponse: MutableLiveData<SignUpResponse> = MutableLiveData()
    val apiResponse: MutableLiveData<SignUpResponse> get() = _apiResponse

    // private val _apiOtpResponse: MutableLiveData<OtpResponse?> = MutableLiveData()
    // val apiOtpResponse: MutableLiveData<OtpResponse?> get() = _apiOtpResponse

    private val _apiOtpResponse: MutableLiveData<OtpResponse?> = MutableLiveData()
    val apiOtpResponse: MutableLiveData<OtpResponse?> get() = _apiOtpResponse


    fun signUp(fullName: String, email: String, phone: String, password: String, token: String) =
        viewModelScope.launch {
            /*  if (!NetworkUtils.isNetworkConnected()) {
                  NetworkUtils.showToast()
                  return@launch
              }*/
            try {
                val response =
                    apiService.signUp(SignUpRequest(fullName, email, phone, password, token))
                if (response.isSuccessful)
                    _apiResponse.value = response.body()
                else
                    println("else in signup-viewModel")
            } catch (e: Exception) {
                println("in catch ${e.message}")
            }
        }

    /*Otp verification */
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
                    val OtpResponse = response.body()
                    _apiOtpResponse.value = OtpResponse
                    Log.d("LoginViewModel", "Login successful: $OtpResponse")
                } else {
                    _apiOtpResponse.value = null
                    Log.e(
                        "LoginViewModel",
                        "Login failed: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                _apiOtpResponse.value = null
                Log.e("LoginViewModel", "Login error: ${t.message}")
            }
        })
    }

}