package com.hulunote.android.data.repository

import com.hulunote.android.data.api.HulunoteApi
import com.hulunote.android.data.model.DatabaseInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepository @Inject constructor(
    private val api: HulunoteApi,
) {
    suspend fun getDatabaseList(): Result<List<DatabaseInfo>> {
        return try {
            val response = api.getDatabaseList()
            Result.success(response.databaseList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
