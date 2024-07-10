package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.HomeCategoryData
import com.antsglobe.restcommerse.model.Response.HomeCategoryResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeCategoryViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getHomeCategory: MutableLiveData<HomeCategoryResponse?> = MutableLiveData()
    val getHomeCategory: MutableLiveData<HomeCategoryResponse?> get() = _getHomeCategory

    private val _getHomeCategoryItem: MutableLiveData<List<HomeCategoryData>> = MutableLiveData()
    val getHomeCategoryItem: LiveData<List<HomeCategoryData>> get() = _getHomeCategoryItem

    fun getHomeCategoryVM(email: String) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            apiService.getHomeCategory(email).enqueue(object :
                Callback<HomeCategoryResponse> {
                override fun onResponse(
                    call: Call<HomeCategoryResponse>,
                    response: Response<HomeCategoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val mostPopular = response.body()
                        _getHomeCategory.value = mostPopular

                        mostPopular?.content?.let { content ->
                            _getHomeCategoryItem.value = content
                        }

                    }
                }

                override fun onFailure(call: Call<HomeCategoryResponse>, t: Throwable) {
                    _getHomeCategory.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}