package com.example.core.data.repository

import android.util.Log
import com.example.core.data.Resource
import com.example.core.data.source.local.LocalDataSource
import com.example.core.data.source.local.entity.PetEntity
import com.example.core.data.source.remote.RemoteDataSource
import com.example.core.data.source.remote.network.ApiResponse
import com.example.core.data.source.remote.response.DataItem
import com.example.core.data.source.remote.response.FormItem
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.data.source.remote.response.VaksinasiItem
import com.example.core.domain.model.AddVaksinasiPet
import com.example.core.domain.model.AddVirtualPet
import com.example.core.domain.model.AddVirtualPetItem
import com.example.core.domain.model.DataAdopt
import com.example.core.domain.model.DataSubmissionFoster
import com.example.core.domain.model.DetailSubmission
import com.example.core.domain.model.DetailSubmissionFoster
import com.example.core.domain.model.FormDetail
import com.example.core.domain.model.MedicalItem
import com.example.core.domain.model.MedicalRecord
import com.example.core.domain.model.PetAdopt
import com.example.core.domain.model.SubmissionItem
import com.example.core.domain.model.VaksinasiData
import com.example.core.domain.model.VirtualPetItem
import com.example.core.domain.repository.IMainRepository
import com.example.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class MainRepository(private val remoteDataSource: RemoteDataSource, private val localDataSource: LocalDataSource): IMainRepository {
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

    override fun formAdopt(token: String, form: FormItem): Flow<Resource<FormDetail>>  = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.formAdopt(token, form).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.formResponseToForm(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("FormAdopt", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getListSubmissionPet(token: String, userId: Int): Flow<Resource<List<SubmissionItem>>> = flow{
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getListSubmissionPet(token, userId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.submissionListPet(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("ListSubmissionPet", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getDetailSubmissionPet(
        token: String,
        reqId: Int
    ): Flow<Resource<DetailSubmission>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getDetailSubmissionPet(token, reqId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.detailSubmissionToSubmission(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("DetailSubmissionPet", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getListSubmissionFoster(
        token: String,
        userId: Int
    ): Flow<Resource<List<DataSubmissionFoster>>>  = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getListSubmissionFoster(token, userId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.submissionListFoster(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("SubmissionFoster", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is  ApiResponse.Empty -> {}
        }
    }

    override fun detailSubmissionFoster(
        token: String,
        reqId: Int
    ): Flow<Resource<DetailSubmissionFoster>> = flow{
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.detailSubmissionFoster(token, reqId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.detailSubmissionFosterToSubmissionFoster(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("DetailSubmissionFoster", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun updateAdopt(
        token: String,
        petId: Int,
        isAdopt: Boolean
    ): Flow<Resource<PetAdopt>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.updateAdopt(token, petId, isAdopt).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.detailDataPetToPet(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("UpdateAdopt", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun updateStatusReq(
        token: String,
        reqId: Int,
        statusReq: Int
    ): Flow<Resource<FormDetail>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.updateStatusReq(token, reqId, statusReq).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.updateFormResponseToForm(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("UpdateAdopt", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun updatePaymentAdopt(
        token: String,
        reqId: Int,
        data: FormItem
    ): Flow<Resource<FormDetail>> = flow{
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.updatePaymentAdopt(token, reqId, data).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.updateFormResponseToForm(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("UpdatePaymentAdopt", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {
            }
        }
    }

    override fun updateStatusPaymentFoster(
        token: String,
        reqId: Int,
        statusPayment: Int
    ): Flow<Resource<FormDetail>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.updateStatusPaymentFoster(token, reqId, statusPayment).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.updateFormResponseToForm(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("UpdateStatusPayment", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun updateStatusPickup(
        token: String,
        reqId: Int,
        data: FormItem
    ): Flow<Resource<FormDetail>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.updateStatusPickup(token, reqId, data).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.updateFormResponseToForm(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("UpdateStatusPickup", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun cancelAdoptUser(
        token: String,
        reqId: Int,
        statusReqId: Int
    ): Flow<Resource<FormDetail>> = flow {
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.cancelAdoptUser(token, reqId, statusReqId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.updateFormResponseToForm(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("CancelAdopt", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun getFavoritePets(): Flow<Resource<List<PetEntity>>> {
        return localDataSource.getFavoritePets()
    }

    override suspend fun insertToFavorite(
        petsEntity: PetEntity,
        favoriteState: Boolean
    ): Flow<Resource<PetEntity>> {
        return localDataSource.insertToFavorite(petsEntity, favoriteState)
    }

    override suspend fun deleteFromFavorite(
        petsEntity: PetEntity,
        favoriteState: Boolean
    ): Flow<Resource<PetEntity>> {
        return localDataSource.deleteFromFavorite(petsEntity, favoriteState)
    }

    override suspend fun getFormDetail(token: String, reqId: Int): Flow<Resource<FormDetail>>  = flow{
        emit(Resource.Loading(true))
        when(val apiResponse = remoteDataSource.getDetailForm(token, reqId).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.formDetailResponseToForm(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                Log.d("FormDetail", "error: ${apiResponse.message}")
                emit(Resource.Error(apiResponse.message))
            }
            is ApiResponse.Empty -> {}
        }
    }

}