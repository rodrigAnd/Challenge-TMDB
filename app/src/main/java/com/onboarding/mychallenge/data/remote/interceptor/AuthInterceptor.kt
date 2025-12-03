package com.onboarding.mychallenge.data.remote.interceptor
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val bearerToken: String,
) : Interceptor {
    companion object {
        private const val TAG = "AuthInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest =
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $bearerToken")
                .header("accept", "application/json")
                .build()
        val response = chain.proceed(newRequest)
        return response
    }
}
