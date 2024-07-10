package com.antsglobe.restcommerse.model.Response

data class GetAllPinCodeResponse(
    val status: Int,
    val message: String?,
    val token: String?,
    val content: List<GetAllPinCodeList>
)

data class GetAllPinCodeList(
    val ID: Int,
    val pin_code: String?,
    val area: String?,
    val state: String?,
    val city: String?,
)