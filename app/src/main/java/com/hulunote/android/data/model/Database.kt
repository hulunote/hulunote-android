package com.hulunote.android.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DatabaseListResponse(
    @Json(name = "database-list") val databaseList: List<DatabaseInfo>,
    @Json(name = "settings") val settings: Map<String, Any>? = null,
)

@JsonClass(generateAdapter = true)
data class DatabaseInfo(
    @Json(name = "hulunote-databases/id") val id: String,
    @Json(name = "hulunote-databases/name") val name: String,
    @Json(name = "hulunote-databases/description") val description: String? = null,
    @Json(name = "hulunote-databases/is-delete") val isDelete: Boolean = false,
    @Json(name = "hulunote-databases/is-public") val isPublic: Boolean = false,
    @Json(name = "hulunote-databases/is-default") val isDefault: Boolean = false,
    @Json(name = "hulunote-databases/account-id") val accountId: Long = 0,
    @Json(name = "hulunote-databases/created-at") val createdAt: String? = null,
    @Json(name = "hulunote-databases/updated-at") val updatedAt: String? = null,
)
