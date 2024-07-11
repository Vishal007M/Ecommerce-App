package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.MostPopularData
import com.antsglobe.restcommerse.model.Response.MostPopularResponse
import com.antsglobe.restcommerse.model.Response.ProductVariationData
import com.antsglobe.restcommerse.model.Response.ProductVariationResponse
import com.antsglobe.restcommerse.model.Response.WishlistResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel(private val apiService: ApiService) : ViewModel() {

    private val _apiProductResponse: MutableLiveData<MostPopularData?>? = MutableLiveData()
    val getProductResponse: MutableLiveData<MostPopularData?>? get() = _apiProductResponse
    private val _variationdata: MutableLiveData<List<ProductVariationData>> = MutableLiveData()
    val variationData: MutableLiveData<List<ProductVariationData>> get() = _variationdata

    fun productListResponse(email: String, productId: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }

        try {
            apiService.getProductDetails(email, productId)
                .enqueue(object : Callback<MostPopularResponse> {
                    override fun onResponse(
                        call: Call<MostPopularResponse>, response: Response<MostPopularResponse>
                    ) {
                        if (response.isSuccessful) {
                            val productListBody = response.body()
                            if (productListBody != null) {
                                Log.d("viewmodel", productListBody.toString())

                                _apiProductResponse?.value = productListBody.content?.get(0)
                            }

                        }
                    }

                    override fun onFailure(call: Call<MostPopularResponse>, t: Throwable) {
                        Log.d("product", t.toString())
                    }
                })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

    fun getproductvariations(productId: String, email: String) {
        viewModelScope.launch {
            apiService.getproductvariations(productId, email)
                .enqueue(object : Callback<ProductVariationResponse> {
                    override fun onResponse(
                        call: Call<ProductVariationResponse>,
                        response: Response<ProductVariationResponse>
                    ) {
                        if (response.isSuccessful) {
                            _variationdata.value = response.body()?.content
                        }
                    }

                    override fun onFailure(call: Call<ProductVariationResponse>, t: Throwable) {
                        Log.d("failed", t.toString())
                    }

                })
        }
    }

    fun addtowishlist(email: String, productId: String) {
        try {
            apiService.addtowishlist(email, productId).enqueue(object : Callback<WishlistResponse> {
                override fun onResponse(
                    call: Call<WishlistResponse>, response: Response<WishlistResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("wishlist", "added")
                    }
                }

                override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                    Log.d("wishlist", "failed")
                }
            })
        } catch (e: Exception) {
            println("in catch${e.message}")
        }
    }

    fun deletefromwishlist(email: String, productId: String) {
        try {
            apiService.deletefromwishlist(email, productId)
                .enqueue(object : Callback<WishlistResponse> {
                    override fun onResponse(
                        call: Call<WishlistResponse>, response: Response<WishlistResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("wishlist", "deleted")
                        }
                    }

                    override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                        Log.d("wishlist", "failed")
                    }
                })
        } catch (e: Exception) {
            println("in catch${e.message}")
        }
    }

}