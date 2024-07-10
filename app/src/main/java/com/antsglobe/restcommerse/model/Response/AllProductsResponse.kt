package com.antsglobe.restcommerse.model.Response

data class AllProductsResponse(
    val token: String?,
    val content: List<AllProductsList>
)

data class AllProductsList(
    val PID: Int,
    val productname: String,
    val product_price: Int,
    val disc_price: Int,
    val disc_percent: Int,
    val product_size: String,
    val product_color: String,
    val short_descrip: String,
    val long_descrip: String,
    val primary_img: String,
    val product_url: String,
    val img_1: String,
    val img1_url: String,
    val img_2: String,
    val img2_url: String,
    val img_3: String,
    val img3_url: String,
    val img_4: String,
    val img4_url: String,
    val cat_id: Int,
    val prod_type: String,
    val prod_tag: String,
    val prod_availability: String,
    val stock_quantity: Int,
    val rating: Double,
    val totalreview: Int,
    val wishlist_status: Boolean,
    val Off_Price: Double? = null

)
