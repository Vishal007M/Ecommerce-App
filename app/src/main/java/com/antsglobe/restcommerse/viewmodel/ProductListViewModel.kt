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
import com.antsglobe.restcommerse.model.Response.ProductList
import com.antsglobe.restcommerse.model.Response.ProductListResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListViewModel(private val apiService: ApiService) : ViewModel() {

    private val _apiProductList: MutableLiveData<List<ProductList?>?> = MutableLiveData()
    val getProductListResponse: MutableLiveData<List<ProductList?>?> get() = _apiProductList

    private val _apiProductListResponse: MutableLiveData<ProductListResponse?> = MutableLiveData()
    val getProductResponse: MutableLiveData<ProductListResponse?> get() = _apiProductListResponse

    private val _cartlistresponse: MutableLiveData<List<CartListData>> = MutableLiveData()

    val cartListResponse: MutableLiveData<List<CartListData>> get() = _cartlistresponse

    private val _cartlistsize: MutableLiveData<Int> = MutableLiveData(0)

    val cartListsize: MutableLiveData<Int> get() = _cartlistsize
    fun productListResponse(email: String, categoryId: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        try {
            apiService.getCategoryProduct(email, categoryId).enqueue(object :
                Callback<ProductListResponse> {
                override fun onResponse(
                    call: Call<ProductListResponse>,
                    response: Response<ProductListResponse>
                ) {
                    if (response.isSuccessful) {
                        val productListBody = response.body()
                        _apiProductListResponse.value = productListBody

                        productListBody?.content?.let { content ->
                            _apiProductList.value = content
                        }
                    }
                }

                override fun onFailure(call: Call<ProductListResponse>, t: Throwable) {
                    _apiProductListResponse.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

    fun getcartlist(email: String) {
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
        email: String?,
        productid: String?,
        orignal_price: String?,
        price: String?,
        quantity: String?,
        total: String?,
        variation_id: String?,
    ) {
        viewModelScope.launch {
            apiService.addToCartApi(email, productid, orignal_price, price, quantity, total, variation_id)
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