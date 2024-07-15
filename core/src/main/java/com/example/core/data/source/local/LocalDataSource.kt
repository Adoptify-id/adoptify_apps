package com.example.core.data.source.local

import android.util.Log
import androidx.lifecycle.asFlow
import com.example.core.data.Resource
import com.example.core.data.source.local.datastore.UserPreferences
import com.example.core.data.source.local.entity.PetEntity
import com.example.core.data.source.local.room.PetsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LocalDataSource(private val userPreferences: UserPreferences, private val petsDao: PetsDao) {
    fun getSession() = userPreferences.getSession()

    fun getUsername() =  userPreferences.getUsername()

    fun getUserId() = userPreferences.getUserId()

    fun getRoleId() = userPreferences.getRoleId()

    suspend fun saveSession(token: String, username: String, userId: Int, roleId: Int) = userPreferences.saveSession(token, username, userId, roleId)

    suspend fun deleteSession() = userPreferences.deleteSession()

    fun getFavoritePets() : Flow<Resource<List<PetEntity>>> = flow {
        emit(Resource.Loading(true))
        try {
            val pet = petsDao.getFavoritePets().asFlow().first()
            emit(Resource.Success(pet))
        } catch (e: Exception) {
            Log.d("LocalDataSource", "Error retrieving favorite pets: ${e.localizedMessage}")
            emit(Resource.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun insertToFavorite(pet: PetEntity, favoriteState: Boolean) : Flow<Resource<PetEntity>> = flow {
        try {
            pet.isFavorite = favoriteState
            petsDao.insertPet(pet)
            emit(Resource.Success(pet))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deleteFromFavorite(pet: PetEntity, favoriteState: Boolean) : Flow<Resource<PetEntity>> = flow {
        try {
            pet.isFavorite = favoriteState
            petsDao.deletePet(pet)
            emit(Resource.Success(pet))
        } catch (e: Exception) {
            Log.d("LocalDataSource", "error : $pet")
            emit(Resource.Error(e.message.toString()))
        }
    }
}