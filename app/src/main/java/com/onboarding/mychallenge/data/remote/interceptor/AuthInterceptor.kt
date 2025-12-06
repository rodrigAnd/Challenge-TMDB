package com.onboarding.mychallenge.data.remote.interceptor
import android.util.Log
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
        Log.d(TAG, "intercept: URL = ${newRequest.url}")
        Log.d(TAG, "intercept: Headers = ${newRequest.headers}")
        val response = chain.proceed(newRequest)
        Log.d(TAG, "intercept: Response Status = ${response.code}")
        return response
    }
}
