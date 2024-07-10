package com.antsglobe.restcommerse.model.Response

data class OffersBannerResponse(
    val token: String? = null,
    val content: List<OffersBannerData?>?
)

data class OffersBannerData(
    val ID: Int,
    val img: String? = null,
    val img_url: String? = null,
    val img_type: String? = null,
    val Category_id: Int
)
