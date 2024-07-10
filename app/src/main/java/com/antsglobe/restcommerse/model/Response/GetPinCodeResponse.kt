package com.antsglobe.restcommerse.model.Response

data class GetPinCodeResponse(
    val deliverable: Boolean?,
    val message: String?,
    val status: Int?
)