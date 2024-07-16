package com.example.core.utils

import android.util.Log
import com.example.core.data.source.local.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userPreferences: UserPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { userPreferences.getSession().first() }
        Log.d("AuthInterceptor", "Using token: $token")

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()


        val response = chain.proceed(newRequest)
        Log.d("Auth", "code: ${response.code}")
        if (response.code == 401) {
            runBlocking {
                userPreferences.deleteSession()
                ForceLogout.notifySessionExpired()
            }
            return response
        }

        return response
    }
}
