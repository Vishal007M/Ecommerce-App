package com.antsglobe.restcommerse.model.Response

data class CartListResponse(
    val token: String? = null,
    val total: String? = null,
    val content: List<CartListData>
)

data class CartListData(

    val ID: Int,
    val product_id: Int,
    val productname: String? = null,
    val orignal_price: Int,
    val dis_price: Int,
    val quantity: Int,
    val total: Int,
    val prod_type: String? = null,
    val size: String? = null,
    val primary_img: String? = null,
    val product_url: String? = null,
    val rating: Double? = null,
    val totalreview: Int?,
    val Off_Price: Int? = null,
    val variation_id: Int


)