package com.antsglobe.restcommerse.model.Response

data class ProductListResponse(
    val token: String?,
    val content: List<ProductList?>?,
)

data class ProductList(
    val PID: Int?,
    val cat_id: Int?,
    val disc_percent: Int?,
    val disc_price: Int?,
    val img1_url: String? = null,
    val img2_url: String? = null,
    val img3_url: String? = null,
    val img4_url: String? = null,
    val img_1: String? = null,
    val img_2: String? = null,
    val img_3: String? = null,
    val img_4: String? = null,
    val long_descrip: String? = null,
    val primary_img: String? = null,
    val prod_availability: String? = null,
    val prod_tag: String? = null,
    val prod_type: String? = null,
    val product_color: String? = null,
    val product_price: Int?,
    val product_size: String? = null,
    val product_url: String? = null,
    val productname: String? = null,
    val short_descrip: String? = null,
    val totalreview: Int?,
    val rating: Double? = null,
    val Off_Price: Double? = null
)