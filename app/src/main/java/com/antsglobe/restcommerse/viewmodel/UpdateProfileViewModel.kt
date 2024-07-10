package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.UpdateProfileResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileViewModel(private val apiService: ApiService) : ViewModel() {

    private val _getUpdateProfile: MutableLiveData<UpdateProfileResponse?> = MutableLiveData()
    val getUpdateProfile: MutableLiveData<UpdateProfileResponse?> get() = _getUpdateProfile

    fun UpdateProfileVM(
        email: String,
        name: String,
        dob: String,
        address: String,
        phone: String,
        gender: String,
        uer_image: String
    ) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        apiService.updateProfile(email, name, dob, address, phone, gender, uer_image)
            .enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val updateProfileResponseList = response.body()
                        _getUpdateProfile.value = updateProfileResponseList
                        Log.d("LoginViewModel", "Login successful: $updateProfileResponseList")
                    } else {
                        _getUpdateProfile.value = null
                        Log.e(
                            "LoginViewModel",
                            "Login failed: ${response.code()} - ${response.message()}"
                        )

                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    _getUpdateProfile.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
    }

}