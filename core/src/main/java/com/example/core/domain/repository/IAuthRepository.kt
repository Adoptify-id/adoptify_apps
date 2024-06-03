package com.example.core.domain.repository

import com.example.core.data.Resource
import com.example.core.data.source.remote.response.DataUser
import com.example.core.domain.model.DetailDataUser
import com.example.core.domain.model.DetailUser
import com.example.core.domain.model.Login
import com.example.core.domain.model.Register
import com.example.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun registerUser(user: User): Flow<Resource<Register>>

    fun login(username: String, password: String) : Flow<Resource<Login>>

    suspend fun saveSession(token: String, username: String, userId: Int, roleId: Int)

    fun updateUser(token: String, userId: Int, data: DataUser) : Flow<Resource<DetailUser>>

    fun getDetailUser(token: String, userId: Int): Flow<Resource<DetailUser>>

    suspend fun deleteSession()

    fun getAuthToken(): Flow<String>

    fun getUsername(): Flow<String>

    fun getUserId(): Flow<Int>

    fun getRoleId(): Flow<Int>
}