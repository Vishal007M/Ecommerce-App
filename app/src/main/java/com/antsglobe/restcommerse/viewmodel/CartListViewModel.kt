package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.AddToCartResponse
import com.antsglobe.restcommerse.model.Response.CartListData
import com.antsglobe.restcommerse.model.Response.CartListResponse
import com.antsglobe.restcommerse.model.Response.DeleteCartResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartListViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getCartList: MutableLiveData<CartListResponse?> = MutableLiveData()
    val getCartList: MutableLiveData<CartListResponse?> get() = _getCartList
    private val _getCartListData: MutableLiveData<List<CartListData>> = MutableLiveData()
    val getCartListData: LiveData<List<CartListData>> get() = _getCartListData
    private val _addCartData: MutableLiveData<AddToCartResponse> = MutableLiveData()
    val addCartData: LiveData<AddToCartResponse> get() = _addCartData

    fun getCartListVM(email: String) = viewModelScope.launch {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return@launch
        }
        try {
            apiService.getCartList(email).enqueue(object :
                Callback<CartListResponse> {
                override fun onResponse(
                    call: Call<CartListResponse>,
                    response: Response<CartListResponse>
                ) {
                    if (response.isSuccessful) {
                        val mostPopular = response.body()
                        _getCartList.value = mostPopular

                        mostPopular?.content?.let { content ->
                            _getCartListData.value = content
                        }
                    }
                }

                override fun onFailure(call: Call<CartListResponse>, t: Throwable) {
                    _getCartList.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

    fun deleteFromCartVM(email: String, pid: String, variation_id: String?) {
        viewModelScope.launch {

            apiService.deleteFromCartList(email, pid, variation_id)
                .enqueue(object : Callback<DeleteCartResponse> {
                    override fun onResponse(
                        call: Call<DeleteCartResponse>,
                        response: Response<DeleteCartResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("from cart", "deleted")
                        }
                    }

                    override fun onFailure(call: Call<DeleteCartResponse>, t: Throwable) {
                        Log.e("LoginViewModel", "Login error: ${t.message}")
                    }

                })
        }
    }

    fun addToCartVM(
        email: String?,
        productid: String?,
        orignal_price: String?,
        dis_price: String?,
        quantity: String?,
        total: String?,
        variation_id: String?,
    ) {

        viewModelScope.launch {
            apiService.addToCartApi(
                email,
                productid,
                orignal_price,
                dis_price,
                quantity,
                total,
                variation_id
            )
                .enqueue(object : Callback<AddToCartResponse> {
                    override fun onResponse(
                        call: Call<AddToCartResponse>,
                        response: Response<AddToCartResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("from cart", "added")
                        }
                    }

                    override fun onFailure(call: Call<AddToCartResponse>, t: Throwable) {
                        Log.e("LoginViewModel", "Login error: ${t.message}")
                    }

                })
        }
    }

}