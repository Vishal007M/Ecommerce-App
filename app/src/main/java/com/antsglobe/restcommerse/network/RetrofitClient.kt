package com.antsglobe.restcommerse.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor())
        .connectTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .build()

    val apiService: ApiService by lazy {

        val restrofit = Retrofit.Builder()
            .baseUrl("https://antsglobe.in/EcomService.asmx/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        restrofit.create(ApiService::class.java)

    }

//     var apiService: ApiService

//    init {
//        apiService = retrofit.create(ApiService::class.java)
//    }
//    var apiService: ApiService by lazy {
//        retrofit.create(ApiService::class.java)
//    }
}
