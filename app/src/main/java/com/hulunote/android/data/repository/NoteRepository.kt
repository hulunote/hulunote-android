package com.hulunote.android.data.repository

import com.hulunote.android.data.api.HulunoteApi
import com.hulunote.android.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val api: HulunoteApi,
) {
    suspend fun getNoteList(databaseId: String, page: Int = 1, size: Int = 100): Result<NoteListResponse> {
        return try {
            val response = api.getNoteList(NoteListRequest(databaseId, page, size))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNote(databaseId: String, title: String): Result<NoteInfo> {
        return try {
            val response = api.createNote(NewNoteRequest(databaseId, title))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNote(noteId: String, title: String? = null, isDelete: Boolean? = null, isShortcut: Boolean? = null): Result<Unit> {
        return try {
            api.updateNote(UpdateNoteRequest(noteId, title, isDelete, isShortcut))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
