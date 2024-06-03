package com.example.core.data.repository

import android.util.Log
import com.example.core.data.Resource
import com.example.core.data.source.local.LocalDataSource
import com.example.core.data.source.remote.RemoteDataSource
import com.example.core.data.source.remote.network.ApiResponse
import com.example.core.data.source.remote.response.DataUser
import com.example.core.domain.model.DetailDataUser
import com.example.core.domain.model.DetailUser
import com.example.core.domain.model.Login
import com.example.core.domain.model.Register
import com.example.core.domain.model.User
import com.example.core.domain.repository.IAuthRepository
import com.example.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AuthRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : IAuthRepository {
    override fun registerUser(user: User): Flow<Resource<Register>> = flow {
        emit(Resource.Loading(true))
        when (val apiResponse = remoteDataSource.registerUser(user).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.registerResponseToRegister(apiResponse.data)
                emit(Resource.Success(data))
            }

            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.message))
            }

            is ApiResponse.Empty -> {}
        }
    }

    override fun login(username: String, password: String): Flow<Resource<Login>> = flow {
        emit(Resource.Loading(true))
        when (val apiResponse = remoteDataSource.loginUser(username, password).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.loginResponseToLogin(apiResponse.data)
                Log.d("Home", "data: $data")
                emit(Resource.Success(data))
            }

            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.message))
            }

            is ApiResponse.Empty -> {}
        }
    }

    override suspend fun saveSession(token: String, username: String, userId: Int, roleId: Int) {
        localDataSource.saveSession(token, username, userId, roleId)
    }

    override suspend fun deleteSession() {
        localDataSource.deleteSession()
    }

    override fun updateUser(
        token: String,
        userId: Int,
        data: DataUser
    ): Flow<Resource<DetailUser>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.updateUser(token, userId, data).first()) {
            is ApiResponse.Success -> {1
                val data = DataMapper.updateUserResponseToUser(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getDetailUser(token: String, userId: Int): Flow<Resource<DetailUser>>  = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getDetailUser(token, userId).first()) {
            is ApiResponse.Success -> {
                val data =  DataMapper.detailDataUserResponseToUser(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getAuthToken(): Flow<String> = localDataSource.getSession()

    override fun getUsername(): Flow<String> = localDataSource.getUsername()

    override fun getUserId(): Flow<Int> = localDataSource.getUserId()

    override fun getRoleId(): Flow<Int> = localDataSource.getRoleId()

}