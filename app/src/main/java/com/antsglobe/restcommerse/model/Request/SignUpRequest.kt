package com.antsglobe.restcommerse.model.Request

data class SignUpRequest(
    val fullname: String,
    val email: String,
    val phone: String,
    val password: String,
    val firetoken: String,
)
