package com.antsglobe.restcommerse.model.Response

data class OrderListResponse(
    val token: String?,
    val content: List<OrderResponse>?
)


data class OrderResponse(
    val email: String?,
    val orderdate: String?,
    val grandtotal: Double,
    val payment_status: String?,
    val order_no: String?,
    val transaction_id: String?,
    val invoiceno: String?,
    val delivery_status: String?,
    val payment_method: String?,
    val item_qty: Int
)
