package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.AllProductsList
import com.antsglobe.restcommerse.model.Response.AllProductsResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllProductListViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getAllProducts: MutableLiveData<AllProductsResponse?> = MutableLiveData()
    val getAllProducts: MutableLiveData<AllProductsResponse?> get() = _getAllProducts

    private val _allProductItems: MutableLiveData<List<AllProductsList>> = MutableLiveData()
    val allProductItems: MutableLiveData<List<AllProductsList>> get() = _allProductItems

    //    private val _cartlistresponse: MutableLiveData<List<CartListData>> = MutableLiveData()
//
//    val cartListResponse: MutableLiveData<List<CartListData>> get() = _cartlistresponse
//
//    private val _cartlistsize: MutableLiveData<Int> = MutableLiveData(0)
//
//    val cartListsize: MutableLiveData<Int> get() = _cartlistsize
    fun allProductListVM(email: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        try {
            apiService.allProductApi(email).enqueue(object :
                Callback<AllProductsResponse> {
                override fun onResponse(
                    call: Call<AllProductsResponse>,
                    response: Response<AllProductsResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        _getAllProducts.value = body

                        body?.content?.let { content ->
                            _allProductItems.value = content
                        }
                    }
                }

                override fun onFailure(call: Call<AllProductsResponse>, t: Throwable) {
                    _getAllProducts.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

//    fun searchProducts(query: String) {
//        val allProducts = _allProductItems.value ?: emptyList()
//
//        val filteredList = if (query.isNotBlank()) {
//            allProducts.filter { product ->
//                product.productname?.contains(query, ignoreCase = true) ?: false
//            }
//        } else {
//            allProducts
//        }
//
//        _allProductItems.value = filteredList
//    }

//    fun getcartlist(email: String) {
//        viewModelScope.launch {
//            apiService.getCartList(email).enqueue(object : Callback<CartListResponse> {
//                override fun onResponse(
//                    call: Call<CartListResponse>,
//                    response: Response<CartListResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        _cartlistresponse.value = response.body()?.content
//                        _cartlistsize.value = response.body()?.content?.size
//                    }
//                }
//
//                override fun onFailure(call: Call<CartListResponse>, t: Throwable) {
//                    Log.d("cartlistinwish", t.toString())
//                }
//
//            })
//        }
//    }
//
//    fun addtocart(
//        email: String?, productid: String?, price: String?,
//        quantity: String?, total: String?
//    ) {
//        viewModelScope.launch {
//            apiService.addToCartApi(email, productid, price, quantity, total)
//                .enqueue(object : Callback<AddToCartResponse> {
//                    override fun onResponse(
//                        call: Call<AddToCartResponse>,
//                        response: Response<AddToCartResponse>
//                    ) {
//                        Log.d("addcartfromwish", response.message().toString())
//                        _cartlistsize.value = _cartlistsize.value?.plus(1)
//                    }
//
//                    override fun onFailure(call: Call<AddToCartResponse>, t: Throwable) {
//                        Log.d("addcartfromwish", t.message.toString())
//                    }
//
//                })
//        }
//    }
//
//    fun deletefromcart(email: String?, productid: String?) {
//        viewModelScope.launch {
//            apiService.deleteFromCartList(email, productid)
//                .enqueue(object : Callback<DeleteCartResponse> {
//                    override fun onResponse(
//                        call: Call<DeleteCartResponse>,
//                        response: Response<DeleteCartResponse>
//                    ) {
//                        Log.d("deletecartfromwish", response.message().toString())
//                        _cartlistsize.value = _cartlistsize.value?.minus(1)
//                    }
//
//                    override fun onFailure(call: Call<DeleteCartResponse>, t: Throwable) {
//                        Log.d("deletecartfromwish", t.message.toString())
//                    }
//
//                })
//        }
//    }

}