package com.antsglobe.restcommerse.model.Response

data class GetAddressList(
    val content: List<AddressList>,
    val token: String?
)

data class AddressList(

    val address: String?,
    val address_type: String?,
    val appartment: String?,
    val city: String?,
    val customer_mobno: String?,
    val customer_name: String?,
    val id: Int?,
    val is_default: Boolean?,
    val landmark: String?,
    val pin: String?,
    val state: String?
)