package com.hulunote.android.util

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        tokenManager.token?.let { token ->
            request.addHeader("X-FUNCTOR-API-TOKEN", token)
        }
        return chain.proceed(request.build())
    }
}
