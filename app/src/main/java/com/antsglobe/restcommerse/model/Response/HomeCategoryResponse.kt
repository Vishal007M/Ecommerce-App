package com.antsglobe.restcommerse.model.Response

data class HomeCategoryResponse(
    val token: String? = null,
    val content: List<HomeCategoryData>
)

data class HomeCategoryData(

    val cid: Int,
    val catname: String? = null,
    val is_active: Boolean,
    val img_url: String? = null,
    val cat_image: String? = null,

    )