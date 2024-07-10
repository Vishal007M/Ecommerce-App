package com.antsglobe.restcommerse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.restcommerse.model.Response.GetVersionList
import com.antsglobe.restcommerse.model.Response.GetVersionResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch

class VersionViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getVersion: MutableLiveData<GetVersionResponse?> = MutableLiveData()
    val getVersion: MutableLiveData<GetVersionResponse?> get() = _getVersion

    private val _getVersionItem: MutableLiveData<List<GetVersionList>> = MutableLiveData()
    val getVersionItem: LiveData<List<GetVersionList>> get() = _getVersionItem


    fun getVersionVM() = viewModelScope.launch {
//        if (!NetworkUtils.isNetworkConnected()) {
//            NetworkUtils.showToast()
//            return@launch
//        }
        try {
            val response = apiService.getVersionApi()
            if (response.isSuccessful) {
                val version = response.body()
                _getVersion.value = version

                version?.content?.let { content ->
                    _getVersionItem.value = content
                }
            }
//                _getVersion.value = response.body()
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}