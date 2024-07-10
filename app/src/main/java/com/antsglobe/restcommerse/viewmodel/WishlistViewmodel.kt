package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.AddToCartResponse
import com.antsglobe.restcommerse.model.Response.CartListData
import com.antsglobe.restcommerse.model.Response.CartListResponse
import com.antsglobe.restcommerse.model.Response.DeleteCartResponse
import com.antsglobe.restcommerse.model.Response.MostPopularData
import com.antsglobe.restcommerse.model.Response.MostPopularResponse
import com.antsglobe.restcommerse.model.Response.WishlistResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishlistViewmodel(private val apiService: ApiService) : ViewModel() {

    private val _apiproductresponse: MutableLiveData<List<MostPopularData?>?> = MutableLiveData()
    val apiresponse: MutableLiveData<List<MostPopularData?>?> get() = _apiproductresponse
    private val _cartlistresponse: MutableLiveData<List<CartListData>> = MutableLiveData()
    val cartListResponse: MutableLiveData<List<CartListData>> get() = _cartlistresponse
    private val _cartlistsize: MutableLiveData<Int> = MutableLiveData(0)
    val cartListsize: MutableLiveData<Int> get() = _cartlistsize

    fun getProductList(email: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        viewModelScope.launch {
            apiService.getwishlist(email).enqueue(object : Callback<MostPopularResponse> {
                override fun onResponse(
                    call: Call<MostPopularResponse>,
                    response: Response<MostPopularResponse>
                ) {
                    if (response.isSuccessful) {
                        val apiresponse = response.body()
                        if (apiresponse != null) {
                            _apiproductresponse.value = apiresponse.content
                        }
                    }
                }

                override fun onFailure(call: Call<MostPopularResponse>, t: Throwable) {
                    Log.d("wishlist", t.toString())
                }

            })
        }
    }

    fun deletefromwishlist(email: String, pid: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        viewModelScope.launch {
            apiService.deletefromwishlist(email, pid).enqueue(object : Callback<WishlistResponse> {
                override fun onResponse(
                    call: Call<WishlistResponse>,
                    response: Response<WishlistResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("from wishlist", "deleted")
                    }
                }

                override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                    Log.d("from wishlist", t.toString())
                }

            })
        }
    }

    fun getcartlist(email: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        viewModelScope.launch {
            apiService.getCartList(email).enqueue(object : Callback<CartListResponse> {
                override fun onResponse(
                    call: Call<CartListResponse>,
                    response: Response<CartListResponse>
                ) {
                    if (response.isSuccessful) {
                        _cartlistresponse.value = response.body()?.content
                        _cartlistsize.value = response.body()?.content?.size
                    }
                }

                override fun onFailure(call: Call<CartListResponse>, t: Throwable) {
                    Log.d("cartlistinwish", t.toString())
                }

            })
        }
    }

    fun addtocart(
        email: String?, productid: String?, orgnal_price: String?, price: String?,
        quantity: String?, total: String?, variation_id: String?
    ) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        viewModelScope.launch {
            apiService.addToCartApi(
                email,
                productid,
                orgnal_price,
                price,
                quantity,
                total,
                variation_id
            )
                .enqueue(object : Callback<AddToCartResponse> {
                    override fun onResponse(
                        call: Call<AddToCartResponse>,
                        response: Response<AddToCartResponse>
                    ) {
                        Log.d("addcartfromwish", response.message().toString())
                        _cartlistsize.value = _cartlistsize.value?.plus(1)
                    }

                    override fun onFailure(call: Call<AddToCartResponse>, t: Throwable) {
                        Log.d("addcartfromwish", t.message.toString())
                    }

                })
        }
    }

    fun deletefromcart(email: String?, productid: String?, variation_id: String?) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        viewModelScope.launch {
            apiService.deleteFromCartList(email, productid, variation_id)
                .enqueue(object : Callback<DeleteCartResponse> {
                    override fun onResponse(
                        call: Call<DeleteCartResponse>,
                        response: Response<DeleteCartResponse>
                    ) {
                        Log.d("deletecartfromwish", response.message().toString())
                        _cartlistsize.value = _cartlistsize.value?.minus(1)
                    }

                    override fun onFailure(call: Call<DeleteCartResponse>, t: Throwable) {
                        Log.d("deletecartfromwish", t.message.toString())
                    }

                })
        }
    }

}