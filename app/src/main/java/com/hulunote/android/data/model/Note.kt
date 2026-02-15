package com.hulunote.android.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NoteListResponse(
    @Json(name = "note-list") val noteList: List<NoteInfo>,
    @Json(name = "all-pages") val allPages: Int? = null,
)

@JsonClass(generateAdapter = true)
data class NoteInfo(
    @Json(name = "hulunote-notes/id") val id: String,
    @Json(name = "hulunote-notes/title") val title: String,
    @Json(name = "hulunote-notes/database-id") val databaseId: String? = null,
    @Json(name = "hulunote-notes/root-nav-id") val rootNavId: String? = null,
    @Json(name = "hulunote-notes/is-delete") val isDelete: Boolean = false,
    @Json(name = "hulunote-notes/is-public") val isPublic: Boolean = false,
    @Json(name = "hulunote-notes/is-shortcut") val isShortcut: Boolean = false,
    @Json(name = "hulunote-notes/account-id") val accountId: Long = 0,
    @Json(name = "hulunote-notes/pv") val pv: Long = 0,
    @Json(name = "hulunote-notes/created-at") val createdAt: String? = null,
    @Json(name = "hulunote-notes/updated-at") val updatedAt: String? = null,
)

@JsonClass(generateAdapter = true)
data class NewNoteRequest(
    @Json(name = "database-id") val databaseId: String,
    @Json(name = "title") val title: String,
)

@JsonClass(generateAdapter = true)
data class NoteListRequest(
    @Json(name = "database-id") val databaseId: String,
    @Json(name = "page") val page: Int = 1,
    @Json(name = "size") val size: Int = 100,
)

@JsonClass(generateAdapter = true)
data class UpdateNoteRequest(
    @Json(name = "note-id") val noteId: String,
    @Json(name = "title") val title: String? = null,
    @Json(name = "is-delete") val isDelete: Boolean? = null,
    @Json(name = "is-shortcut") val isShortcut: Boolean? = null,
)
