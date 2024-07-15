package com.example.core.data.source.remote

import android.util.Log
import com.example.core.data.source.local.LocalDataSource
import com.example.core.data.source.remote.network.ApiResponse
import com.example.core.data.source.remote.network.ApiService
import com.example.core.data.source.remote.response.AddMedicalResponse
import com.example.core.data.source.remote.response.AddPetAdoptResponse
import com.example.core.data.source.remote.response.AddVaksinasiResponse
import com.example.core.data.source.remote.response.AddVirtualPetResponse
import com.example.core.data.source.remote.response.DataItem
import com.example.core.data.source.remote.response.DataSubmission
import com.example.core.data.source.remote.response.DataUser
import com.example.core.data.source.remote.response.DetailSubmissionFosterResponse
import com.example.core.data.source.remote.response.DetailSubmissionResponse
import com.example.core.data.source.remote.response.FormAdoptResponse
import com.example.core.data.source.remote.response.FormDetailResponse
import com.example.core.data.source.remote.response.FormItem
import com.example.core.data.source.remote.response.ItemSubmissionFoster
import com.example.core.data.source.remote.response.LoginResponse
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.data.source.remote.response.PetAdoptResponse
import com.example.core.data.source.remote.response.RegisterResponse
import com.example.core.data.source.remote.response.UserResponse
import com.example.core.data.source.remote.response.VaksinasiItem
import com.example.core.data.source.remote.response.VirtualPetItem
import com.example.core.domain.model.AddVirtualPetItem
import com.example.core.domain.model.User
import com.example.core.utils.DataMapper
import com.example.core.utils.DataMapper.parseErrorMessage
import com.example.core.utils.ForceLogout
import com.example.core.utils.serializeToMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class RemoteDataSource(
    private val apiService: ApiService,
    private val localDataSource: LocalDataSource
) {
    suspend fun registerUser(user: User): Flow<ApiResponse<RegisterResponse>> = flow {
        try {
            val userMap = DataMapper.userMap(user).serializeToMap()
            val response = apiService.register(userMap)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 400) {
                val errorMessage = "Username sudah terpakai. Silakan gunakan username lain."
                emit(ApiResponse.Error(errorMessage))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.e("Register", "Error : $errorMessage")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun loginUser(username: String, password: String): Flow<ApiResponse<LoginResponse>> =
        flow {
            try {
                val response = apiService.login(username, password)
                Log.d("Login", "username : $response ")
                emit(ApiResponse.Success(response))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    val errorMessage = "Username atau password salah. Silakan coba lagi."
                    emit(ApiResponse.Error(errorMessage))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Log.e("Login", "Error : ${e.message.toString()}")
                    emit(ApiResponse.Error(errorMessage))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getVirtualPet(token: String, userId: Int): Flow<ApiResponse<List<VirtualPetItem>>> =
        flow {
            try {
                val response = apiService.getVirtualPet("Bearer $token", userId)
                Log.d("VirtualPet", "result: $response")
                emit(ApiResponse.Success(response.virtualPet))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    localDataSource.deleteSession()
                    ForceLogout.logoutLiveData.postValue(Unit)
                    emit(ApiResponse.Error(e.message().toString()))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Log.d("VirtualPet", "result: $errorMessage")
                    emit(ApiResponse.Error(errorMessage))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun insertVirtualPet(
        token: String,
        data: AddVirtualPetItem
    ): Flow<ApiResponse<AddVirtualPetResponse>> = flow {
        try {
            val virtualPet = DataMapper.virtualPetMap(data)
            val response = apiService.insertVirtualPet("Bearer $token", virtualPet)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("VirtualPet", "result: $errorMessage")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getVaksinasi(token: String, userId: Int): Flow<ApiResponse<List<VaksinasiItem>>> =
        flow {
            try {
                val response = apiService.getVaksinasi("Bearer $token", userId)
                Log.d("Vaksinasi", "result: $response")
                emit(ApiResponse.Success(response.data))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    localDataSource.deleteSession()
                    ForceLogout.logoutLiveData.postValue(Unit)
                    emit(ApiResponse.Error(e.message().toString()))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Log.d("Vaksinasi", "result: $errorMessage")
                    emit(ApiResponse.Error(errorMessage))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun insertVaksinasi(
        token: String,
        data: VaksinasiItem
    ): Flow<ApiResponse<AddVaksinasiResponse>> = flow {
        try {
            val vaksinasi = DataMapper.vaksinasiMap(data).serializeToMap()
            val response = apiService.insertVaksinasi("Bearer $token", vaksinasi)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("InserVaksinasi", "result: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateUser(
        token: String,
        userId: Int,
        data: DataUser
    ): Flow<ApiResponse<UserResponse>> = flow {
        try {
            val data = DataMapper.updateUserMapping(data)
            val response = apiService.updateProfile("Bearer $token", userId, data)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("UpdateProfil", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDetailUser(token: String, userId: Int): Flow<ApiResponse<UserResponse>> = flow {
        try {
            val response = apiService.getDetailProfile("Bearer $token", userId)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("DetailProfile", "error: $errorMessage")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getMedicalRecord(token: String, userId: Int): Flow<ApiResponse<List<DataItem>>> =
        flow {
            try {
                val response = apiService.getMedicalRecord("Bearer $token", userId)
                emit(ApiResponse.Success(response.data))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    localDataSource.deleteSession()
                    ForceLogout.logoutLiveData.postValue(Unit)
                    emit(ApiResponse.Error(e.message().toString()))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Log.d("MedicalRecord", "error: $errorMessage")
                    emit(ApiResponse.Error(errorMessage))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun insertMedicalRecord(
        token: String,
        data: DataItem
    ): Flow<ApiResponse<AddMedicalResponse>> = flow {
        try {
            val medical = DataMapper.medicalItemMap(data)
            val response = apiService.insertMedicalRecord("Bearer $token", medical)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("MedicalRecord", "error: ${e.message.toString()} ")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun insertPetAdopt(
        token: String,
        data: PetAdoptItem
    ): Flow<ApiResponse<AddPetAdoptResponse>> = flow<ApiResponse<AddPetAdoptResponse>> {
        try {
            val pet = DataMapper.petAdoptItemMap(data)
            val response = apiService.insertPetAdopt("Bearer $token", pet)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("PetAdopt", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getListPet(token: String): Flow<ApiResponse<List<PetAdoptItem>>> = flow {
        try {
            val response = apiService.getListPet("Bearer $token")
            emit(ApiResponse.Success(response.data))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("ListPet", "error: $errorMessage")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDetailPet(token: String, petId: Int): Flow<ApiResponse<PetAdoptResponse>> =
        flow {
            try {
                val response = apiService.getDetailPet("Bearer $token", petId)
                emit(ApiResponse.Success(response))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    localDataSource.deleteSession()
                    ForceLogout.logoutLiveData.postValue(Unit)
                    emit(ApiResponse.Error(e.message().toString()))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Log.d("DetailPet", "error: $errorMessage")
                    emit(ApiResponse.Error(errorMessage))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun updatePet(
        token: String,
        petId: Int,
        data: PetAdoptItem
    ): Flow<ApiResponse<PetAdoptResponse>> = flow {
        try {
            val pet = DataMapper.petAdoptItemMap(data)
            val response = apiService.updatePet("Bearer $token", petId, pet)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("UpdatePet", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getPetByUser(token: String, userId: Int): Flow<ApiResponse<List<PetAdoptItem>>> =
        flow {
            try {
                val response = apiService.getPetByUser("Bearer $token", userId)
                emit(ApiResponse.Success(response.data))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    localDataSource.deleteSession()
                    ForceLogout.logoutLiveData.postValue(Unit)
                    emit(ApiResponse.Error(e.message().toString()))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Log.d("ListPet", "error: $errorMessage")
                    emit(ApiResponse.Error(errorMessage))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun refreshToken(token: String): Flow<ApiResponse<LoginResponse>> = flow {
        try {
            val response = apiService.refreshToken("Bearer $token")
            Log.d("Auth", "response: $response")
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("Auth", "error: $errorMessage")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun formAdopt(token: String, form: FormItem): Flow<ApiResponse<FormAdoptResponse>> =
        flow {
            try {
                val data = DataMapper.formItemAdoptMap(form)
                val response = apiService.formAdopt("Bearer $token", data)
                emit(ApiResponse.Success(response))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    localDataSource.deleteSession()
                    ForceLogout.logoutLiveData.postValue(Unit)
                    emit(ApiResponse.Error(e.message().toString()))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Log.d("FormAdopt", "error: ${e.message.toString()}")
                    emit(ApiResponse.Error(errorMessage))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getListSubmissionPet(
        token: String,
        userId: Int
    ): Flow<ApiResponse<List<DataSubmission>>> = flow {
        try {
            val response = apiService.getListSubmissionPet("Bearer $token", userId)
            emit(ApiResponse.Success(response.data))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("ListSubmissionPet", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDetailSubmissionPet(
        token: String,
        reqId: Int
    ): Flow<ApiResponse<DetailSubmissionResponse>> = flow {
        try {
            val response = apiService.getDetailSubmissionPet("Bearer $token", reqId)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("DetailSubmissionPet", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getListSubmissionFoster(
        token: String,
        userId: Int
    ): Flow<ApiResponse<List<ItemSubmissionFoster>>> = flow {
        try {
            val response = apiService.getListSubmissionFoster("Bearer $token", userId)
            emit(ApiResponse.Success(response.data))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("DetailSubmissionPet", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun detailSubmissionFoster(
        token: String,
        reqId: Int
    ): Flow<ApiResponse<DetailSubmissionFosterResponse>> =
        flow {
            try {
                val response = apiService.detailSubmissionFoster("Bearer $token", reqId)
                emit(ApiResponse.Success(response))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    localDataSource.deleteSession()
                    ForceLogout.logoutLiveData.postValue(Unit)
                    emit(ApiResponse.Error(e.message().toString()))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Log.d("DetailSubmissionPet", "error: ${e.message.toString()}")
                    emit(ApiResponse.Error(errorMessage))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun updateAdopt(
        token: String,
        petId: Int,
        isAdopt: Boolean
    ): Flow<ApiResponse<PetAdoptResponse>> = flow {
        try {
            val response = apiService.updateAdopt("Bearer $token", petId, isAdopt)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("DetailSubmissionPet", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateStatusReq(
        token: String,
        reqId: Int,
        statusReq: Int
    ): Flow<ApiResponse<FormDetailResponse>> = flow {
        try {
            val response = apiService.updateStatusReq("Bearer $token", reqId, statusReq)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("DetailSubmissionPet", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updatePaymentAdopt(
        token: String,
        reqId: Int,
        data: FormItem
    ): Flow<ApiResponse<FormDetailResponse>> = flow {
        try {
            val data = DataMapper.updateFormPayment(data)
            val response = apiService.updatePaymentAdopt("Bearer $token", reqId, data)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("DetailSubmissionPet", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateStatusPaymentFoster(
        token: String,
        reqId: Int,
        statusPayment: Int
    ): Flow<ApiResponse<FormDetailResponse>> = flow {
        try {
            val response =
                apiService.updateStatusPaymentFoster("Bearer $token", reqId, statusPayment)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("UpdateStatusPayment", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateStatusPickup(
        token: String,
        reqId: Int,
        data: FormItem
    ): Flow<ApiResponse<FormDetailResponse>> = flow<ApiResponse<FormDetailResponse>> {
        try {
            val data = DataMapper.updatePickup(data)
            val response = apiService.updateStatusPickup(token, reqId, data)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("UpdateStatusPayment", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun cancelAdoptUser(
        token: String,
        reqId: Int,
        statusReqId: Int
    ): Flow<ApiResponse<FormDetailResponse>> = flow {
        try {
            val response = apiService.cancelAdoptUser("Bearer $token", reqId, statusReqId)
            emit(ApiResponse.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                localDataSource.deleteSession()
                ForceLogout.logoutLiveData.postValue(Unit)
                emit(ApiResponse.Error(e.message().toString()))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Log.d("UpdateStatusPayment", "error: ${e.message.toString()}")
                emit(ApiResponse.Error(errorMessage))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDetailForm(token: String, reqId: Int): Flow<ApiResponse<FormDetailResponse>> =
        flow {
            try {
                val response = apiService.getFormDetail("Bearer $token", reqId)
                emit(ApiResponse.Success(response))
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    localDataSource.deleteSession()
                    ForceLogout.logoutLiveData.postValue(Unit)
                    emit(ApiResponse.Error(e.message().toString()))
                } else {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Log.d("FormDetail", "error: ${e.message.toString()}")
                    emit(ApiResponse.Error(errorMessage))
                }
            }
        }.flowOn(Dispatchers.IO)
}