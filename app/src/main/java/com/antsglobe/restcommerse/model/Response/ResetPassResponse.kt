package com.antsglobe.restcommerse.model.Response

data class ResetPassResponse(

    val is_success: String,
    val message: String,
    val email: String,
    val token: String,
)