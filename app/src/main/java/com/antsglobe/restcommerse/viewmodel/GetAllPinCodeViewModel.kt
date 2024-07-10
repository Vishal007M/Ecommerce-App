package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.GetAllPinCodeList
import com.antsglobe.restcommerse.model.Response.GetAllPinCodeResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetAllPinCodeViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getAllPincode: MutableLiveData<GetAllPinCodeResponse?> = MutableLiveData()
    val getAllPincode: MutableLiveData<GetAllPinCodeResponse?> get() = _getAllPincode

    private val _getAllPincodeList: MutableLiveData<List<GetAllPinCodeList>> = MutableLiveData()
    val getAllPincodeList: LiveData<List<GetAllPinCodeList>> get() = _getAllPincodeList

    fun getAllPincodeVM(email: String) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            apiService.getAllPinCodeApi(email).enqueue(object :
                Callback<GetAllPinCodeResponse> {
                override fun onResponse(
                    call: Call<GetAllPinCodeResponse>,
                    response: Response<GetAllPinCodeResponse>
                ) {
                    if (response.isSuccessful) {
                        val mostPopular = response.body()
                        _getAllPincode.value = mostPopular

                        mostPopular?.content?.let { content ->
                            _getAllPincodeList.value = content
                        }

                    }
                }

                override fun onFailure(call: Call<GetAllPinCodeResponse>, t: Throwable) {
                    _getAllPincode.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}