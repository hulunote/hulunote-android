package com.hulunote.android.data.repository

import com.hulunote.android.data.api.HulunoteApi
import com.hulunote.android.data.model.LoginRequest
import com.hulunote.android.data.model.LoginResponse
import com.hulunote.android.util.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: HulunoteApi,
    private val tokenManager: TokenManager,
) {
    val isLoggedIn: Boolean get() = tokenManager.isLoggedIn

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            tokenManager.token = response.token
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.logout()
    }
}
