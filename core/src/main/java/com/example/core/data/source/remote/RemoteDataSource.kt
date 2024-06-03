package com.example.core.data.source.remote

import android.util.Log
import androidx.datastore.preferences.protobuf.Api
import com.example.core.data.Resource
import com.example.core.data.source.remote.network.ApiResponse
import com.example.core.data.source.remote.network.ApiService
import com.example.core.data.source.remote.response.AddMedicalResponse
import com.example.core.data.source.remote.response.AddPetAdoptResponse
import com.example.core.data.source.remote.response.AddVaksinasiResponse
import com.example.core.data.source.remote.response.AddVirtualPetResponse
import com.example.core.data.source.remote.response.DataItem
import com.example.core.data.source.remote.response.DataUser
import com.example.core.data.source.remote.response.LoginResponse
import com.example.core.data.source.remote.response.MedicalRecordResponse
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.data.source.remote.response.PetAdoptResponse
import com.example.core.data.source.remote.response.RegisterResponse
import com.example.core.data.source.remote.response.UserResponse
import com.example.core.data.source.remote.response.VaksinasiItem
import com.example.core.data.source.remote.response.VirtualPetItem
import com.example.core.domain.model.AddVirtualPetItem
import com.example.core.domain.model.DetailDataUser
import com.example.core.domain.model.User
import com.example.core.utils.DataMapper
import com.example.core.utils.serializeToMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSource(private val apiService: ApiService) {
    suspend fun registerUser(user: User): Flow<ApiResponse<RegisterResponse>> = flow {
        try {
            val userMap = DataMapper.userMap(user).serializeToMap()
            val response = apiService.register(userMap)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.e("Register", "Error : ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun loginUser(username: String, password: String): Flow<ApiResponse<LoginResponse>> =
        flow {
            try {
                val response = apiService.login(username, password)
                Log.d("Login", "username : $response ")
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                Log.e("Login", "Error : ${e.message.toString()}")
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getVirtualPet(token: String, userId: Int): Flow<ApiResponse<List<VirtualPetItem>>> = flow {
        try {
            val response = apiService.getVirtualPet("Bearer $token", userId)
            Log.d("VirtualPet", "result: $response")
            emit(ApiResponse.Success(response.virtualPet))
        } catch (e: Exception) {
            Log.d("VirtualPet", "result: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun insertVirtualPet(token: String, data: AddVirtualPetItem): Flow<ApiResponse<AddVirtualPetResponse>> = flow {
        try {
            val virtualPet = DataMapper.virtualPetMap(data)
            val response = apiService.insertVirtualPet("Bearer $token", virtualPet)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.d("VirtualPet", "error : ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getVaksinasi(token: String, userId: Int) : Flow<ApiResponse<List<VaksinasiItem>>> = flow {
        try {
            val response = apiService.getVaksinasi("Bearer $token", userId)
            Log.d("Vaksinasi", "result: $response")
            emit(ApiResponse.Success(response.data))
        } catch (e: Exception) {
            Log.d("Vaksinasi", "error: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun insertVaksinasi(token: String, data: VaksinasiItem) : Flow<ApiResponse<AddVaksinasiResponse>> = flow {
        try {
            val vaksinasi = DataMapper.vaksinasiMap(data).serializeToMap()
            val response = apiService.insertVaksinasi("Bearer $token", vaksinasi)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.d("InserVaksinasi", "result: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateUser(token: String, userId: Int, data: DataUser) : Flow<ApiResponse<UserResponse>> = flow {
        try {
            val data = DataMapper.updateUserMapping(data)
            val response = apiService.updateProfile("Bearer $token",userId, data)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.d("UpdateProfil", "error: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDetailUser(token: String, userId: Int) : Flow<ApiResponse<UserResponse>> = flow {
        try {
           val response = apiService.getDetailProfile("Bearer $token", userId)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.d("DetailProfile", "error: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getMedicalRecord(token: String, userId: Int): Flow<ApiResponse<List<DataItem>>> = flow {
        try {
            val response = apiService.getMedicalRecord("Bearer $token", userId)
            emit(ApiResponse.Success(response.data))
        } catch (e: Exception) {
            Log.d("MedicalRecord", "error: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun insertMedicalRecord(token: String, data: DataItem) : Flow<ApiResponse<AddMedicalResponse>> = flow {
        try {
            val medical = DataMapper.medicalItemMap(data)
            val response = apiService.insertMedicalRecord("Bearer $token", medical)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.d("MedicalRecord", "error: ${e.message.toString()} ")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun insertPetAdopt(token: String, data: PetAdoptItem) : Flow<ApiResponse<AddPetAdoptResponse>> = flow<ApiResponse<AddPetAdoptResponse>> {
        try {
            val pet = DataMapper.petAdoptItemMap(data)
            val response = apiService.insertPetAdopt("Bearer $token", pet)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.d("PetAdopt", "error: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getListPet(token: String): Flow<ApiResponse<List<PetAdoptItem>>> = flow {
        try {
            val response = apiService.getListPet("Bearer $token")
            emit(ApiResponse.Success(response.data))
        } catch (e: Exception) {
            Log.d("ListPet", "error: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDetailPet(token: String, petId: Int) : Flow<ApiResponse<PetAdoptResponse>> = flow {
        try {
            val response = apiService.getDetailPet("Bearer $token", petId)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.d("DetailPet", "error: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updatePet(token: String, petId: Int, data: PetAdoptItem) : Flow<ApiResponse<PetAdoptResponse>> = flow {
        try {
            val pet = DataMapper.petAdoptItemMap(data)
            val response = apiService.updatePet("Bearer $token", petId, pet)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.d("UpdatePet", "error: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getPetByUser(token: String, userId: Int) : Flow<ApiResponse<List<PetAdoptItem>>> = flow {
        try {
            val response = apiService.getPetByUser("Bearer $token", userId)
            emit(ApiResponse.Success(response.data))
        } catch (e: Exception) {
            Log.d("ListPet", "error: ${e.message.toString()}")
            emit(ApiResponse.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}