package com.antsglobe.restcommerse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.model.Response.AddAddressResponse
import com.antsglobe.restcommerse.model.Response.GetPinCodeResponse
import com.antsglobe.restcommerse.model.Response.SetDefaultAddressResponse
import com.antsglobe.restcommerse.model.Response.UpdatedAddressResponse
import com.antsglobe.restcommerse.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressViewModel(private val apiService: ApiService) : ViewModel() {

    private val _addAddressResponse: MutableLiveData<AddAddressResponse?> = MutableLiveData()
    val addAddressResponse: MutableLiveData<AddAddressResponse?> get() = _addAddressResponse

    private val _updatedAddressResponse: MutableLiveData<UpdatedAddressResponse?> =
        MutableLiveData()
    val updatedAddressResponse: MutableLiveData<UpdatedAddressResponse?> get() = _updatedAddressResponse

    private val _defaultAddressResponse: MutableLiveData<SetDefaultAddressResponse?> =
        MutableLiveData()
    val defaultAddressResponse: MutableLiveData<SetDefaultAddressResponse?> get() = _defaultAddressResponse

    private val _pinCodeResponse: MutableLiveData<GetPinCodeResponse?> = MutableLiveData()
    val pinCodeResponse: MutableLiveData<GetPinCodeResponse?> get() = _pinCodeResponse
    fun AddAddressResponse(
        email: String, addressType: String, address: String, isDefault: String,
        appartment: String, landmark: String, city: String, state: String,
        pin: String, customerName: String, customerMobno: String
    ) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        apiService.addCustomerAddress(
            email, addressType, address, isDefault, appartment, landmark,
            city, state, pin, customerName, customerMobno
        ).enqueue(object :
            Callback<AddAddressResponse> {
            override fun onResponse(
                call: Call<AddAddressResponse>,
                response: Response<AddAddressResponse>
            ) {
                if (response.isSuccessful) {
                    val addReviewResponse = response.body()
                    _addAddressResponse.value = addReviewResponse
                    Log.d("addAddress", "Add address successful: ${addReviewResponse}")
                } else {
                    _addAddressResponse.value = null
                    Log.e(
                        "addAddress",
                        "Add address  failed: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<AddAddressResponse>, t: Throwable) {
                _addAddressResponse.value = null
                Log.e("AddressViewModel", "Add address error: ${t.message}")
            }
        })
    }

    fun UpdateAddressResponse(
        email: String, addressId: String, addressType: String, address: String, isDefault: String,
        appartment: String, landmark: String, city: String, state: String,
        pin: String, customerName: String, customerMobno: String
    ) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        apiService.updateCustomerAddress(
            email, addressId, addressType, address, isDefault, appartment, landmark,
            city, state, pin, customerName, customerMobno
        ).enqueue(object :
            Callback<UpdatedAddressResponse> {
            override fun onResponse(
                call: Call<UpdatedAddressResponse>,
                response: Response<UpdatedAddressResponse>
            ) {
                if (response.isSuccessful) {
                    val addReviewResponse = response.body()
                    _updatedAddressResponse.value = addReviewResponse
                    Log.d("updateAddress", "Update address successful: ${addReviewResponse}")
                } else {
                    _updatedAddressResponse.value = null
                    Log.e(
                        "updateAddress",
                        "Update address  failed: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<UpdatedAddressResponse>, t: Throwable) {
                _updatedAddressResponse.value = null
                Log.e("AddressViewModel", "Add address error: ${t.message}")
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
                } else {
                    _defaultAddressResponse.value = null
                    Log.e(
                        "AddressListViewModel",
                        "Default Address  failed: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<SetDefaultAddressResponse>, t: Throwable) {
                _defaultAddressResponse.value = null
                Log.e("AddressListViewModel", "Default Address error: ${t.message}")
            }
        })
    }


    fun PinCodeResponse(email: String, pinCode: String) {
        if (!NetworkUtils.isNetworkConnected()) {
            NetworkUtils.showToast()
            return
        }
        apiService.getPinCodeApi(email, pinCode).enqueue(object :
            Callback<GetPinCodeResponse> {
            override fun onResponse(
                call: Call<GetPinCodeResponse>,
                response: Response<GetPinCodeResponse>
            ) {
                if (response.isSuccessful) {
                    val addReviewResponse = response.body()
                    _pinCodeResponse.value = addReviewResponse
                    Log.d("_pinCodeResponse", "_pinCodeResponse successful: ${addReviewResponse}")
                } else {
                    _pinCodeResponse.value = null
                    Log.e(
                        "_pinCodeResponse",
                        "_pinCodeResponse failed: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<GetPinCodeResponse>, t: Throwable) {
                _pinCodeResponse.value = null
                Log.e("AddressViewModel", "Add address error: ${t.message}")
            }
        })
    }

}