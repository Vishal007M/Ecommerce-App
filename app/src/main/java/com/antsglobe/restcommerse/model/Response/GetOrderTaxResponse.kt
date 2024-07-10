package com.antsglobe.restcommerse.model.Response

data class GetOrderTaxResponse(
    val is_success: Boolean?,
    val message: String? = null,
    val email: String? = null,
    val total_price: String? = null,
    val promodisc: Int?,
    val shipcharge: Int?,
    val other_disc: Int?,
    val taxableAmt: String? = null,
    val taxper: String? = null,
    val taxamt: String? = null,
    val grandtotal: String? = null,
)