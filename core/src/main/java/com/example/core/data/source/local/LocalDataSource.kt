package com.example.core.data.source.local

import com.example.core.data.source.local.datastore.UserPreferences

class LocalDataSource(private val userPreferences: UserPreferences) {
    fun getSession() = userPreferences.getSession()

    fun getUsername() =  userPreferences.getUsername()

    fun getUserId() = userPreferences.getUserId()

    fun getRoleId() = userPreferences.getRoleId()

    suspend fun saveSession(token: String, username: String, userId: Int, roleId: Int) = userPreferences.saveSession(token, username, userId, roleId)

    suspend fun deleteSession() = userPreferences.deleteSession()

}