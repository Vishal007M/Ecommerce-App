package com.antsglobe.restcommerse.model.Response

data class MostPopularResponse(
    val token: String? = null,
    val content: List<MostPopularData?>?
)

data class MostPopularData(

    val PID: Int,
    val productname: String? = null,
    val product_price: Int,
    val disc_price: Int,
    val disc_percent: Int,
    val product_size: String? = null,
    val product_color: String? = null,
    val short_descrip: String? = null,
    val long_descrip: String? = null,
    val primary_img: String? = null,
    val product_url: String? = null,
    val img_1: String? = null,
    val img1_url: String? = null,
    val img_2: String? = null,
    val img2_url: String? = null,
    val img_3: String? = null,
    val img3_url: String? = null,
    val img_4: String? = null,
    val img4_url: String? = null,
    val cat_id: Int,
    val prod_type: String? = null,
    val prod_tag: String? = null,
    val prod_availability: String? = null,
    val rating: Double,
    val totalreview: Int,
    val wishlist_status: Boolean,
    val Off_Price: Double? = null,
    val is_variant: Boolean,
    val totalquantity: Double? = null,

    )