package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.AddressList
import com.antsglobe.restcommerse.model.Response.DeleteAddressResponse
import com.antsglobe.restcommerse.model.Response.GetAddressList
import com.antsglobe.restcommerse.model.Response.SetDefaultAddressResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressListViewModel(private val apiService: ApiService) : ViewModel() {

    private val _apiGetAddressListResponse: MutableLiveData<GetAddressList?> = MutableLiveData()
    val getGetAddressResponse: MutableLiveData<GetAddressList?> get() = _apiGetAddressListResponse
    private val _apiAddressListResponse: MutableLiveData<List<AddressList>> = MutableLiveData()
    val getAddressListResponse: MutableLiveData<List<AddressList>> get() = _apiAddressListResponse
    private val _deleteAddressResponse: MutableLiveData<DeleteAddressResponse?> = MutableLiveData()
    val deleteAddressResponse: MutableLiveData<DeleteAddressResponse?> get() = _deleteAddressResponse
    private val _defaultAddressResponse: MutableLiveData<SetDefaultAddressResponse?> =
        MutableLiveData()
    val defaultAddressResponse: MutableLiveData<SetDefaultAddressResponse?> get() = _defaultAddressResponse

    fun getAddressResponse(email: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        try {
            apiService.getAddressList(email).enqueue(object :
                Callback<GetAddressList> {
                override fun onResponse(
                    call: Call<GetAddressList>,
                    response: Response<GetAddressList>
                ) {
                    if (response.isSuccessful) {
                        val addressBody = response.body()
                        _apiGetAddressListResponse.value = addressBody

                        addressBody?.content?.let {
                            _apiAddressListResponse.value = it
                        }
                    }
                }

                override fun onFailure(call: Call<GetAddressList>, t: Throwable) {
                    _apiGetAddressListResponse.value = null
                    Log.e("AddressModel", "Address error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

    fun DeleteAddressResponse(email: String, addressId: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        apiService.deleteAddress(email, addressId).enqueue(object :
            Callback<DeleteAddressResponse> {
            override fun onResponse(
                call: Call<DeleteAddressResponse>,
                response: Response<DeleteAddressResponse>
            ) {
                if (response.isSuccessful) {
                    val addReviewResponse = response.body()
                    _deleteAddressResponse.value = addReviewResponse
                    Log.d("AddressListViewModel", "Delete address successful: ${addReviewResponse}")
                }
            }

            override fun onFailure(call: Call<DeleteAddressResponse>, t: Throwable) {
                _deleteAddressResponse.value = null
                Log.e("AddressListViewModel", "Delete Address error: ${t.message}")
            }
        })
    }

    fun DefaultAddressResponse(email: String, addressId: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        apiService.defaultAddress(email, addressId).enqueue(object :
            Callback<SetDefaultAddressResponse> {
            override fun onResponse(
                call: Call<SetDefaultAddressResponse>,
                response: Response<SetDefaultAddressResponse>
            ) {
                if (response.isSuccessful) {
                    val addReviewResponse = response.body()
                    _defaultAddressResponse.value = addReviewResponse
                    Log.d(
                        "AddressListViewModel",
                        "Default address successful: ${addReviewResponse}"
                    )
                }
            }

            override fun onFailure(call: Call<SetDefaultAddressResponse>, t: Throwable) {
                _defaultAddressResponse.value = null
                Log.e("AddressListViewModel", "Default Address error: ${t.message}")
            }
        })
    }

}