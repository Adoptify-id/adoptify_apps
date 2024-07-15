package com.example.core.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    fun getSession(): Flow<String> = dataStore.data.map { preferences ->
        preferences[TOKEN] ?: ""
    }

    fun getUsername(): Flow<String> = dataStore.data.map { preferences ->
        preferences[USERNAME] ?: ""
    }

    fun getUserId(): Flow<Int> = dataStore.data.map { preferences ->
        preferences[USER_ID] ?: -1
    }

    fun getRoleId(): Flow<Int> = dataStore.data.map { preferences ->
        preferences[ROLE_ID] ?: -1
    }

    suspend fun saveSession(token: String, username: String, userId: Int, roleId: Int) = dataStore.edit { preferences ->
        preferences[TOKEN] = token
        preferences[USERNAME] = username
        preferences[IS_LOGIN] = true
        preferences[USER_ID] = userId
        preferences[ROLE_ID] = roleId
    }

    suspend fun deleteSession() = dataStore.edit {
        it.clear()
    }

    suspend fun saveLastBackgroundTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_BACKGROUND_TIME] = time
        }
    }

    val lastBackgroundTime: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[LAST_BACKGROUND_TIME] ?: 0L
        }

    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val IS_LOGIN = booleanPreferencesKey("isLogin")
        private val USERNAME = stringPreferencesKey("username")
        private val USER_ID = intPreferencesKey("userId")
        private val ROLE_ID = intPreferencesKey("roleId")
        private val LAST_BACKGROUND_TIME = longPreferencesKey("last_background_time")
    }
}