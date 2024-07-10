package com.antsglobe.restcommerse.model.Response

data class GetVersionResponse(
    val content: List<GetVersionList>,
)

data class GetVersionList(
    val version_code: String
)