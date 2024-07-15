package com.example.core.utils

import android.util.Log
import com.example.core.data.source.local.datastore.UserPreferences
import com.example.core.data.source.remote.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SessionManager(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
) {
    private val refreshInterval = 10 * 60 * 1000L

    init {
        CoroutineScope(Dispatchers.IO).launch {
            userPreferences.getSession().collect { token ->
                if (token.isNotEmpty()) {
                    while (true) {
                        delay(refreshInterval)
                        Log.d("SessionManager", "Refreshing token")
                        refreshToken(token)
                    }
                } else {
                    Log.d("SessionManager", "Token is empty, not starting refresh loop")
                }
            }
        }
    }

    private suspend fun refreshToken(token: String) {
        try {
            val response = apiService.refreshToken("Bearer $token")
            if (response != null) {
                Log.d("SessionManager", "New token: ${response.accessToken}")
                userPreferences.saveSession(
                    response.accessToken,
                    response.username,
                    response.userId,
                    response.roleId
                )
            } else {
                Log.e("SessionManager", "Failed to refresh token: response is null")
            }
        } catch (e: Exception) {
            Log.e("SessionManager", "Error refreshing token", e)
        }
    }
}
