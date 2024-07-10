package com.antsglobe.restcommerse.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    // private val sharedPrefManager = PreferenceManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = "YWVyb2xpdGUxMzU3OQ=="
        // Log.e("tu", "intercept: ${sharedPrefManager.getAccessToken()}", )

        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "YWVyb2xpdGUxMzU3OQ==")
                .build()
        } else {
            originalRequest
        }
        return try {
            chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }
    }
}
