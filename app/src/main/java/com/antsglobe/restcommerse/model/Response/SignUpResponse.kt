package com.antsglobe.restcommerse.model.Response

data class SignUpResponse(
    val email: String,
    val is_success: Boolean,
    val message: String,
    val authtoken: String? = null,
    val firetoken: String? = null
)