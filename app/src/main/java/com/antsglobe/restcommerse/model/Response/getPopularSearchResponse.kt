package com.antsglobe.restcommerse.model.Response


data class getPopularSearchResponse(
    val token: String?,
    val content: List<PopularSearchList>?
)

data class PopularSearchList(
    val cid: String?,
    val catname: String?,
)