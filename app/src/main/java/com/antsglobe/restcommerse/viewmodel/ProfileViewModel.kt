package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.ProfileData
import com.antsglobe.restcommerse.model.Response.ProfileResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getProfile: MutableLiveData<ProfileResponse?> = MutableLiveData()
    val getProfile: MutableLiveData<ProfileResponse?> get() = _getProfile

    private val _getProfileItem: MutableLiveData<List<ProfileData>> = MutableLiveData()
    val getProfileItem: LiveData<List<ProfileData>> get() = _getProfileItem

    fun getProfileVM(email: String) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            apiService.getProfile(email).enqueue(object :
                Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val profileBody = response.body()
                        _getProfile.value = profileBody

                        profileBody?.content?.let { content ->
                            _getProfileItem.value = content
                        }

                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    _getProfile.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}