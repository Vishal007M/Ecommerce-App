package com.antsglobe.restcommerse.model.Response

data class ProfileResponse(
    val status: String,
    val message: String,
    val authtoken: String,
    val firetoken: String,
    val content: List<ProfileData>
)

data class ProfileData(

    val ID: Int,
    val name: String? = null,
    val email: String? = null,
    val mobno: String? = null,
    val dob: String? = null,
    val address: String? = null,
    val gender: String? = null,
    val token: String? = null,
    val firebase_token: String? = null,
    val uer_image: String? = null,
)