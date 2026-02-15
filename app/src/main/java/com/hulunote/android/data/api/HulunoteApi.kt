package com.hulunote.android.data.api

import com.hulunote.android.data.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface HulunoteApi {

    // Auth
    @POST("login/web-login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Database
    @POST("hulunote/get-database-list")
    suspend fun getDatabaseList(@Body body: Map<String, String> = emptyMap()): DatabaseListResponse

    // Notes
    @POST("hulunote/get-note-list")
    suspend fun getNoteList(@Body request: NoteListRequest): NoteListResponse

    @POST("hulunote/new-note")
    suspend fun createNote(@Body request: NewNoteRequest): NoteInfo

    @POST("hulunote/update-hulunote-note")
    suspend fun updateNote(@Body request: UpdateNoteRequest): Map<String, Any>

    // Navs (Outline blocks)
    @POST("hulunote/get-note-navs")
    suspend fun getNavList(@Body request: NavListRequest): NavListResponse

    @POST("hulunote/create-or-update-nav")
    suspend fun createOrUpdateNav(@Body request: NavCreateRequest): NavCreateResponse
}
