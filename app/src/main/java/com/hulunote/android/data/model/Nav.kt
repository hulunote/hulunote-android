package com.hulunote.android.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NavListResponse(
    @Json(name = "nav-list") val navList: List<NavInfo>,
)

@JsonClass(generateAdapter = true)
data class NavInfo(
    @Json(name = "id") val id: String,
    @Json(name = "parid") val parid: String? = null,
    @Json(name = "same-deep-order") val sameDeepOrder: Float = 0f,
    @Json(name = "content") val content: String = "",
    @Json(name = "account-id") val accountId: Long = 0,
    @Json(name = "last-account-id") val lastAccountId: Long? = null,
    @Json(name = "note-id") val noteId: String? = null,
    @Json(name = "hulunote-note") val hulunoteNote: String? = null,
    @Json(name = "database-id") val databaseId: String? = null,
    @Json(name = "is-display") val isDisplay: Boolean = true,
    @Json(name = "is-public") val isPublic: Boolean = false,
    @Json(name = "is-delete") val isDelete: Boolean = false,
    @Json(name = "properties") val properties: String? = null,
    @Json(name = "created-at") val createdAt: String? = null,
    @Json(name = "updated-at") val updatedAt: String? = null,
)

@JsonClass(generateAdapter = true)
data class NavCreateRequest(
    @Json(name = "note-id") val noteId: String,
    @Json(name = "id") val id: String? = null,
    @Json(name = "parid") val parid: String? = null,
    @Json(name = "content") val content: String? = null,
    @Json(name = "is-delete") val isDelete: Boolean? = null,
    @Json(name = "is-display") val isDisplay: Boolean? = null,
    @Json(name = "order") val order: Float? = null,
)

@JsonClass(generateAdapter = true)
data class NavCreateResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "id") val id: String? = null,
    @Json(name = "nav") val nav: NavInfo? = null,
    @Json(name = "backend-ts") val backendTs: Long? = null,
)

@JsonClass(generateAdapter = true)
data class NavListRequest(
    @Json(name = "note-id") val noteId: String,
)
