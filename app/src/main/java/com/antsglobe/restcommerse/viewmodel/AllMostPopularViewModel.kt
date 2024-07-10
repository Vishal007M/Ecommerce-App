package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.MostPopularData
import com.antsglobe.restcommerse.model.Response.MostPopularResponse
import com.antsglobe.restcommerse.model.Response.WishlistResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllMostPopularViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getMostPopular: MutableLiveData<MostPopularResponse?> = MutableLiveData()
    val getMostPopular: MutableLiveData<MostPopularResponse?> get() = _getMostPopular

    private val _getMostPopularItem: MutableLiveData<List<MostPopularData?>?> = MutableLiveData()
    val getMostPopularItem: LiveData<List<MostPopularData?>?> get() = _getMostPopularItem

    fun getAllMostPopularVM(email: String) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            apiService.getMostPopular(email).enqueue(object :
                Callback<MostPopularResponse> {
                override fun onResponse(
                    call: Call<MostPopularResponse>,
                    response: Response<MostPopularResponse>
                ) {
                    if (response.isSuccessful) {
                        val mostPopular = response.body()
                        _getMostPopular?.value = mostPopular

                        mostPopular?.content?.let { content ->
                            _getMostPopularItem?.value = content
                        }

                    }
                }

                override fun onFailure(call: Call<MostPopularResponse>, t: Throwable) {
                    _getMostPopular.value = null
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
                    Log.d("toppop", "failed")
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
                    Log.d("toppop", "failed")
                }
            }
            )
        } catch (e: Exception) {
            println("in catch${e.message}")
        }
    }

}