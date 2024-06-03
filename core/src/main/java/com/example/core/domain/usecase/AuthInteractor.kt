package com.example.core.domain.usecase

import com.example.core.data.Resource
import com.example.core.data.source.remote.response.DataUser
import com.example.core.domain.model.DetailDataUser
import com.example.core.domain.model.DetailUser
import com.example.core.domain.model.Login
import com.example.core.domain.model.Register
import com.example.core.domain.model.User
import com.example.core.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class AuthInteractor(private val authRepository: IAuthRepository) : AuthUseCase {
    override fun registerUser(user: User): Flow<Resource<Register>> = authRepository.registerUser(user)

    override fun login(username: String, password: String): Flow<Resource<Login>> = authRepository.login(username, password)

    override suspend fun saveSession(token: String, username: String, userId: Int, roleId: Int) = authRepository.saveSession(token, username, userId, roleId)

    override fun updateUser(
        token: String,
        userId: Int,
        data: DataUser
    ): Flow<Resource<DetailUser>> = authRepository.updateUser(token, userId, data)

    override fun getDetailUser(token: String, userId: Int): Flow<Resource<DetailUser>> = authRepository.getDetailUser(token, userId)
    override suspend fun deleteSession() = authRepository.deleteSession()

    override fun getAuthToken(): Flow<String> = authRepository.getAuthToken()

    override fun getUsername(): Flow<String> = authRepository.getUsername()

    override fun getUserId(): Flow<Int> = authRepository.getUserId()

    override fun getRoleId(): Flow<Int> = authRepository.getRoleId()

}