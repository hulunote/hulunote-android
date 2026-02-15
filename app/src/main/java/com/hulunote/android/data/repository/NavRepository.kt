package com.hulunote.android.data.repository

import com.hulunote.android.data.api.HulunoteApi
import com.hulunote.android.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavRepository @Inject constructor(
    private val api: HulunoteApi,
) {
    suspend fun getNavList(noteId: String): Result<List<NavInfo>> {
        return try {
            val response = api.getNavList(NavListRequest(noteId))
            Result.success(response.navList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNav(
        noteId: String,
        parid: String?,
        content: String,
        order: Float?,
    ): Result<NavCreateResponse> {
        return try {
            val response = api.createOrUpdateNav(
                NavCreateRequest(
                    noteId = noteId,
                    parid = parid,
                    content = content,
                    order = order,
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNav(
        noteId: String,
        navId: String,
        content: String? = null,
        parid: String? = null,
        order: Float? = null,
        isDelete: Boolean? = null,
        isDisplay: Boolean? = null,
    ): Result<NavCreateResponse> {
        return try {
            val response = api.createOrUpdateNav(
                NavCreateRequest(
                    noteId = noteId,
                    id = navId,
                    parid = parid,
                    content = content,
                    isDelete = isDelete,
                    isDisplay = isDisplay,
                    order = order,
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
