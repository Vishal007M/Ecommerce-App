package com.antsglobe.restcommerse.model.Response

data class ProductVariationResponse(
    val token: String? = null,
    val content: List<ProductVariationData>
)

data class ProductVariationData(
    val Variation_id: Int,
    val product_id: Int,
    val size: String? = null,
    val product_price: Double,
    val discount_price: Double,
    val off_price: Double
)
