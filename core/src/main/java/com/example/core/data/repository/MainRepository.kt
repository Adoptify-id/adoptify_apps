package com.example.core.data.repository

import android.util.Log
import androidx.datastore.preferences.protobuf.Api
import com.example.core.data.Resource
import com.example.core.data.source.remote.RemoteDataSource
import com.example.core.data.source.remote.network.ApiResponse
import com.example.core.data.source.remote.response.AddVirtualPetResponse
import com.example.core.data.source.remote.response.DataItem
import com.example.core.data.source.remote.response.DataUser
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.data.source.remote.response.VaksinasiItem
import com.example.core.data.source.remote.response.VirtualPetResponse
import com.example.core.domain.model.AddVaksinasiPet
import com.example.core.domain.model.AddVirtualPet
import com.example.core.domain.model.AddVirtualPetItem
import com.example.core.domain.model.DataAdopt
import com.example.core.domain.model.DetailDataUser
import com.example.core.domain.model.DetailUser
import com.example.core.domain.model.MedicalItem
import com.example.core.domain.model.MedicalRecord
import com.example.core.domain.model.PetAdopt
import com.example.core.domain.model.VaksinasiData
import com.example.core.domain.model.VirtualPet
import com.example.core.domain.model.VirtualPetItem
import com.example.core.domain.repository.IMainRepository
import com.example.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class MainRepository(private val remoteDataSource: RemoteDataSource): IMainRepository {
    override fun getVirtualPet(token: String, userId: Int): Flow<Resource<List<VirtualPetItem>>> = flow {
        emit(Resource.Loading(true))
        when (val apiResponse = remoteDataSource.getVirtualPet(token, userId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.virtualPetItemToVirtualPet(apiResponse.data)
                emit(Resource.Success(data))
            }

            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.message))
            }

            is ApiResponse.Empty -> {}
        }
    }

    override fun insertVirtualPet(token: String, data: AddVirtualPetItem): Flow<Resource<AddVirtualPet>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.insertVirtualPet(token, data).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.virtualPetResponseToVirtualPet(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getVaksinasi(token: String, userId: Int): Flow<Resource<List<VaksinasiData>>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getVaksinasi(token, userId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.vakasinasiItemToVaksinasi(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun insertVaksinasi(
        token: String,
        data: VaksinasiItem
    ): Flow<Resource<AddVaksinasiPet>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.insertVaksinasi(token, data).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.vaksinasiResponseToVaksinasi(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getMedicalRecord(token: String, userId: Int): Flow<Resource<List<MedicalItem>>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getMedicalRecord(token, userId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.medicalItemToMedical(apiResponse.data)
                emit(Resource.Success(data))
            }

            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }

            is ApiResponse.Empty -> {}
        }
    }

    override fun insertMedicalRecord(token: String, data: DataItem): Flow<Resource<MedicalRecord>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.insertMedicalRecord(token, data).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.medicalResponseToMedical(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }
            is ApiResponse.Empty -> {}
        }
    }

    override fun insertPetAdopt(token: String, data: PetAdoptItem): Flow<Resource<PetAdopt>>  = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.insertPetAdopt(token, data).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.petResponseToPet(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getListPet(token: String): Flow<Resource<List<DataAdopt>>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getListPet(token).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.petAdoptItemToPet(apiResponse.data)
                emit(Resource.Success(data))
            }

            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getDetailPet(token: String, petId: Int): Flow<Resource<PetAdopt>> = flow{
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getDetailPet(token, petId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.detailDataPetToPet(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }
            is ApiResponse.Empty -> {}
        }
    }

    override fun updatePet(
        token: String,
        petId: Int,
        data: PetAdoptItem
    ): Flow<Resource<PetAdopt>>  = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.updatePet(token, petId, data).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.detailDataPetToPet(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getPetByUser(token: String, userId: Int): Flow<Resource<List<DataAdopt>>> = flow{
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getPetByUser(token, userId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.petAdoptItemToPet(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> { emit(Resource.Error(apiResponse.message)) }
            is ApiResponse.Empty -> {}
        }
    }

}