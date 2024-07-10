package com.antsglobe.restcommerse.model.Response

data class NotificationResponse(
    val Total: String,
    val read: String,
    val unread: String,
    val content: List<NotificationList>
)

data class NotificationList(

    val ID: Int,
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
    val type: String? = null,
    val create_date: String? = null,
    val isactive: Boolean? = null,
    val isread: Boolean? = null,

    )