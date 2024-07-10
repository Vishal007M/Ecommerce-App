package com.antsglobe.restcommerse.model.Response

data class GetRecentSearchResponse(
    val token: String?,
    val content: List<RecentSearchList>?
)

data class RecentSearchList(
    val email: String?,
    val product_name: String?,
)