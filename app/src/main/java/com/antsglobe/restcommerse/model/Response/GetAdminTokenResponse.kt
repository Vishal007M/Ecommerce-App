package com.antsglobe.restcommerse.model.Response

data class GetAdminTokenResponse(
    val content: List<GetAdminTokenLl>
)

data class GetAdminTokenLl(
    val token: String? = null,
)