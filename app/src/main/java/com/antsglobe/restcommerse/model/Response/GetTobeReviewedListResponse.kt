package com.antsglobe.restcommerse.model.Response

data class GetTobeReviewedListResponse(
    val token: String? = null,
    val content: List<TobeReviewedList>
)

data class TobeReviewedList(
    val product_id: Int,
    val productname: String? = null,
    val primary_img: String? = null,
    val product_url: String? = null,
    val orderdate: String? = null,

    )