package com.antsglobe.restcommerse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsglobe.restcommerse.model.Response.NotificationList
import com.antsglobe.restcommerse.model.Response.NotificationResponse
import com.antsglobe.restcommerse.network.ApiService
import kotlinx.coroutines.launch

class NotificationViewModel(private val apiService: ApiService) : ViewModel() {


    private val _getNotification: MutableLiveData<NotificationResponse?> = MutableLiveData()
    val getNotification: MutableLiveData<NotificationResponse?> get() = _getNotification

    private val _getNotificationItem: MutableLiveData<List<NotificationList>> = MutableLiveData()
    val getNotificationItem: LiveData<List<NotificationList>> get() = _getNotificationItem

    fun getAllNotifications() = viewModelScope.launch {
//        if (!NetworkUtils.isNetworkConnected()) {
//            NetworkUtils.showToast()
//            return@launch
//        }
        try {
            val response = apiService.getNotification()
            if (response.isSuccessful) {
                val notification = response.body()
                _getNotification.value = notification

                notification?.content?.let { content ->
                    _getNotificationItem.value = content
                }

            }
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}