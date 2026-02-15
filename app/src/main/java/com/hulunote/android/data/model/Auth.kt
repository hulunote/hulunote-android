package com.hulunote.android.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "token") val token: String,
    @Json(name = "hulunote") val hulunote: AccountInfo,
    @Json(name = "region") val region: String? = null,
)

@JsonClass(generateAdapter = true)
data class AccountInfo(
    @Json(name = "accounts/id") val id: Long,
    @Json(name = "accounts/username") val username: String? = null,
    @Json(name = "accounts/nickname") val nickname: String? = null,
    @Json(name = "accounts/mail") val mail: String? = null,
    @Json(name = "accounts/invitation-code") val invitationCode: String? = null,
    @Json(name = "accounts/is-new-user") val isNewUser: Boolean = false,
    @Json(name = "accounts/created-at") val createdAt: String? = null,
    @Json(name = "accounts/updated-at") val updatedAt: String? = null,
)
