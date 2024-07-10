package com.antsglobe.restcommerse.model.Response

data class CouponListResponse(
    val token: String?,
    val content: List<Coupon>
)


data class Coupon(
    val ID: Int,
    val coupon_name: String?,
    val disc_percent: Double,
    val valid_from: String?,
    val valid_to: String?,
    val is_active: Boolean,
    val comment: String?,
    val coupon_code: String?,
    val coupon_category: String?,
)
