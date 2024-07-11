package com.antsglobe.restcommerse.model.Response

data class GetOrderDetailsResponse(
    val orderDetails: List<OrderDetail?>?,
    val orderMaster: OrderMaster?,
    val token: String?
)

data class OrderMaster(
    val address_type: String?,
    val appartment: String?,
    val delivary_date: String?,
    val delivery_status: String?,
    val grandtotal: Double?,
    val landmark: String?,
    val mobno: String?,
    val orderdate: String?,
    val orderstatus: String?,
    val payment_method: String?,
    val promodisc: Double?,
    val shipaddress: String?,
    val shipcharge: Int?,
    val shipcity: String?,
    val shipname: String?,
    val shipstate: String?,
    val shipzipcode: String?,
    val taxamt: Double?,
    val taxper: Int?,
    val total_price: Double?,
    val transaction_id: String?
)


data class OrderDetail(
    val camnt: Int?,
    val dis_price: Int?,
    val original_price: Int?,
    val prodid: Int?,
    val productname: String?,
    val size: String?,
    val quantity: Int?,
    val subtotal: Int?,
    val variation_id: Int?
)