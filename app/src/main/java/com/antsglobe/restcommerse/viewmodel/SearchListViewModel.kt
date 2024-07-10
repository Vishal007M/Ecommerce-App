package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.GetRecentSearchResponse
import com.antsglobe.restcommerse.model.Response.PopularSearchList
import com.antsglobe.restcommerse.model.Response.RecentSearchList
import com.antsglobe.restcommerse.model.Response.RecentSearchResponse
import com.antsglobe.restcommerse.model.Response.getPopularSearchResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchListViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getRecentSearchList: MutableLiveData<GetRecentSearchResponse?> = MutableLiveData()
    val getRecentSearchList: MutableLiveData<GetRecentSearchResponse?> get() = _getRecentSearchList

    private val _getRecentSearchListData: MutableLiveData<List<RecentSearchList>> =
        MutableLiveData()
    val getRecentSearchListData: LiveData<List<RecentSearchList>> get() = _getRecentSearchListData

    private val _addSearchData: MutableLiveData<RecentSearchResponse> = MutableLiveData()
    val addSearchData: LiveData<RecentSearchResponse> get() = _addSearchData


    private val _getPopularSearchList: MutableLiveData<getPopularSearchResponse?> =
        MutableLiveData()
    val getPopularSearchList: MutableLiveData<getPopularSearchResponse?> get() = _getPopularSearchList

    private val _getPopularSearchListData: MutableLiveData<List<PopularSearchList>> =
        MutableLiveData()
    val getPopularSearchListData: LiveData<List<PopularSearchList>> get() = _getPopularSearchListData


    fun addRecentSearchVM(product_name: String?, email: String?) {

        viewModelScope.launch {
            apiService.addRecentProductsApi(product_name, email)
                .enqueue(object : Callback<RecentSearchResponse> {
                    override fun onResponse(
                        call: Call<RecentSearchResponse>,
                        response: Response<RecentSearchResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("from cart", "added")
                        }
                    }

                    override fun onFailure(call: Call<RecentSearchResponse>, t: Throwable) {
                        Log.e("LoginViewModel", "Login error: ${t.message}")
                    }

                })
        }


    }


    fun getRecentSearchListVM(email: String) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            apiService.getRecentProductApi(email).enqueue(object :
                Callback<GetRecentSearchResponse> {
                override fun onResponse(
                    call: Call<GetRecentSearchResponse>,
                    response: Response<GetRecentSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val mostPopular = response.body()
                        _getRecentSearchList.value = mostPopular

                        mostPopular?.content?.let { content ->
                            _getRecentSearchListData.value = content
                        }

                    }
                }

                override fun onFailure(call: Call<GetRecentSearchResponse>, t: Throwable) {
                    _getRecentSearchList.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

    fun getPopularSearchListVM(email: String) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            apiService.getPopularSearchApi(email).enqueue(object :
                Callback<getPopularSearchResponse> {
                override fun onResponse(
                    call: Call<getPopularSearchResponse>,
                    response: Response<getPopularSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val mostPopular = response.body()
                        _getPopularSearchList.value = mostPopular

                        mostPopular?.content?.let { content ->
                            _getPopularSearchListData.value = content
                        }

                    }
                }

                override fun onFailure(call: Call<getPopularSearchResponse>, t: Throwable) {
                    _getPopularSearchList.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }


}