package com.antsglobe.restcommerse.model.Response

data class GetReviewedListResponse(
    val token: String? = null,
    val content: List<ReviewedList>
)

data class ReviewedList(
    val email: String? = null,
    val product_id: Int,
    val productname: String? = null,
    val primary_img: String? = null,
    val product_url: String? = null,
    val orderdate: String? = null,
    val cust_review: String? = null,
    val cust_rating: Double,
    val review_date: String? = null,
    val review_time: String? = null,
    )