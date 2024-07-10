package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.OffersBannerData
import com.antsglobe.restcommerse.model.Response.OffersBannerResponse
import com.antsglobe.restcommerse.model.Response.TopMostPopularData
import com.antsglobe.restcommerse.model.Response.TopMostPopularResponse
import com.antsglobe.restcommerse.model.Response.WishlistResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopMostPopularViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getTopMostPopular: MutableLiveData<TopMostPopularResponse?> = MutableLiveData()
    val getTopMostPopular: MutableLiveData<TopMostPopularResponse?> get() = _getTopMostPopular

    private val _getTopMostPopularItem: MutableLiveData<List<TopMostPopularData>> =
        MutableLiveData()
    val getTopMostPopularItem: LiveData<List<TopMostPopularData>> get() = _getTopMostPopularItem

    private val _banners: MutableLiveData<List<OffersBannerData?>?> = MutableLiveData()

    val banners: MutableLiveData<List<OffersBannerData?>?> get() = _banners

    fun getTopMostPopularVM(email: String) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            apiService.getTopMostPopular(email).enqueue(object :
                Callback<TopMostPopularResponse> {
                override fun onResponse(
                    call: Call<TopMostPopularResponse>,
                    response: Response<TopMostPopularResponse>
                ) {
                    if (response.isSuccessful) {
                        val mostPopular = response.body()
                        _getTopMostPopular.value = mostPopular

                        mostPopular?.content?.let { content ->
                            _getTopMostPopularItem.value = content
                        }

                    }
                }

                override fun onFailure(call: Call<TopMostPopularResponse>, t: Throwable) {
                    _getTopMostPopular.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

    fun addtowishlist(email: String, productId: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        try {
            apiService.addtowishlist(email, productId).enqueue(object :
                Callback<WishlistResponse> {
                override fun onResponse(
                    call: Call<WishlistResponse>,
                    response: Response<WishlistResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("toppop", "added")
                    }
                }

                override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                    Log.d("toppop", t.message.toString())
                }
            }
            )
        } catch (e: Exception) {
            println("in catch${e.message}")
        }
    }

    fun deletefromwishlist(email: String, productId: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        try {
            apiService.deletefromwishlist(email, productId).enqueue(object :
                Callback<WishlistResponse> {
                override fun onResponse(
                    call: Call<WishlistResponse>,
                    response: Response<WishlistResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("toppop", "deleted")
                    }
                }

                override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                    Log.d("toppop", t.message.toString())
                }
            }
            )
        } catch (e: Exception) {
            println("in catch${e.message}")
        }
    }

    fun getofferbanners(email: String) {
        viewModelScope.launch {
            apiService.getofferbanners(email).enqueue(object : Callback<OffersBannerResponse> {
                override fun onResponse(
                    call: Call<OffersBannerResponse>,
                    response: Response<OffersBannerResponse>
                ) {
                    if (response.isSuccessful) {
                        _banners.value = response.body()?.content
                    }
                }

                override fun onFailure(call: Call<OffersBannerResponse>, t: Throwable) {
                    Log.d("failedgettingbanners", t.toString())
                }

            })
        }
    }
}