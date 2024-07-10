package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.CategoriesList
import com.antsglobe.restcommerse.model.Response.CategoriesResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllCategoryViewModel(private val apiService: ApiService) : ViewModel() {

    private val _apiAllCategoryListResponse: MutableLiveData<List<CategoriesList>?> =
        MutableLiveData()
    val getAllCategoryListResponse: MutableLiveData<List<CategoriesList>?> get() = _apiAllCategoryListResponse

    private val _apiAllCategoryResponse: MutableLiveData<CategoriesResponse?> = MutableLiveData()
    val getAllCategoryResponse: MutableLiveData<CategoriesResponse?> get() = _apiAllCategoryResponse

    fun allCategory(email: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        try {
            apiService.getAllCategories(email).enqueue(object :
                Callback<CategoriesResponse> {
                override fun onResponse(
                    call: Call<CategoriesResponse>,
                    response: Response<CategoriesResponse>
                ) {
                    if (response.isSuccessful) {
                        val categoryBody = response.body()
                        _apiAllCategoryResponse.value = categoryBody

                        categoryBody?.content?.let { content ->
                            _apiAllCategoryListResponse.value = content
                        }
                    }
                }

                override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                    _apiAllCategoryResponse.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }
}