package com.antsglobe.restcommerse.model.Response

data class LoginResponse(
    val status: String,
    val message: String,
    val token: String,
    val name: String,
    val email: String,
    val mobno: String,
    val deviceid: String,
)
