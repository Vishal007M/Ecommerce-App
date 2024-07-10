package com.antsglobe.restcommerse.model.Response

data class CategoriesResponse(
    val token: String,
    val content: List<CategoriesList>
)

data class CategoriesList(

    val cid: Int,
    val catname: String? = null,
    val is_active: String? = null,
    val img_url: String? = null,
    val cat_image: String? = null,
)