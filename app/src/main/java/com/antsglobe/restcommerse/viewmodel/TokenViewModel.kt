package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.GetFCMTokenResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getUserToken: MutableLiveData<GetFCMTokenResponse?> = MutableLiveData()
    val getUserToken: MutableLiveData<GetFCMTokenResponse?> get() = _getUserToken

    fun getUserTokenVM(
        email: String,
        Fcm_Token: String,
    ) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            val response = apiService.AddUserFirebaseTokenApi(email, Fcm_Token)
                .enqueue(object : Callback<GetFCMTokenResponse> {
                    override fun onResponse(
                        call: Call<GetFCMTokenResponse>,
                        response: Response<GetFCMTokenResponse>
                    ) {
                        if (response.isSuccessful) {
                            val notification = response.body()
                            _getUserToken.value = notification


                        }
                    }

                    override fun onFailure(call: Call<GetFCMTokenResponse>, t: Throwable) {
                        _getUserToken.value = null
                        Log.e("LoginViewModel", "Login error: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}