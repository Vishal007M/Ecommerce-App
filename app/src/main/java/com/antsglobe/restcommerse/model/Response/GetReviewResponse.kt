package com.antsglobe.restcommerse.model.Response

data class GetReviewResponse(
    val content: List<ReviewList?>?,
    val rating: String?,
    val star1: String?,
    val star2: String?,
    val star3: String?,
    val star4: String?,
    val star5: String?,
    val totalreview: String?
)

data class ReviewList(
    val cust_rating: Double?,
    val cust_review: String?,
    val email: String?,
    val name: String?,
    val product_id: Int?,
    val review_date: String?,
    val review_time: String?
)