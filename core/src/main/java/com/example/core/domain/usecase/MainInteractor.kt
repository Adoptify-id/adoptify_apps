package com.example.core.domain.usecase

import com.example.core.data.Resource
import com.example.core.data.source.local.entity.PetEntity
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
import kotlinx.coroutines.flow.Flow

class MainInteractor(private val mainRepository: IMainRepository): MainUseCase {
    override fun getVirtualPet(token: String, userId: Int): Flow<Resource<List<VirtualPetItem>>> =
        mainRepository.getVirtualPet(token, userId)

    override fun insertVirtualPet(
        token: String,
        data: AddVirtualPetItem
    ): Flow<Resource<AddVirtualPet>> = mainRepository.insertVirtualPet(token, data)

    override fun getVaksinasi(token: String, userId: Int): Flow<Resource<List<VaksinasiData>>> =
        mainRepository.getVaksinasi(token, userId)

    override fun insertVaksinasi(
        token: String,
        data: VaksinasiItem
    ): Flow<Resource<AddVaksinasiPet>> = mainRepository.insertVaksinasi(token, data)

    override fun getMedicalRecord(token: String, userId: Int): Flow<Resource<List<MedicalItem>>> = mainRepository.getMedicalRecord(token, userId)
    override fun insertMedicalRecord(token: String, data: DataItem): Flow<Resource<MedicalRecord>> = mainRepository.insertMedicalRecord(token, data)
    override fun insertPetAdopt(token: String, data: PetAdoptItem): Flow<Resource<PetAdopt>> = mainRepository.insertPetAdopt(token, data)
    override fun getListPet(token: String): Flow<Resource<List<DataAdopt>>> = mainRepository.getListPet(token)
    override fun getDetailPet(token: String, petId: Int): Flow<Resource<PetAdopt>> = mainRepository.getDetailPet(token, petId)
    override fun updatePet(
        token: String,
        petId: Int,
        data: PetAdoptItem
    ): Flow<Resource<PetAdopt>> = mainRepository.updatePet(token, petId, data)

    override fun getPetByUser(token: String, userId: Int): Flow<Resource<List<DataAdopt>>> = mainRepository.getPetByUser(token, userId)

    override fun formAdopt(token: String, form: FormItem): Flow<Resource<FormDetail>> = mainRepository.formAdopt(token, form)

    override fun getListSubmissionPet(token: String, userId: Int): Flow<Resource<List<SubmissionItem>>> = mainRepository.getListSubmissionPet(token, userId)
    override fun getDetailSubmissionPet(
        token: String,
        reqId: Int
    ): Flow<Resource<DetailSubmission>> = mainRepository.getDetailSubmissionPet(token, reqId)

    override fun getListSubmissionFoster(
        token: String,
        userId: Int
    ): Flow<Resource<List<DataSubmissionFoster>>> = mainRepository.getListSubmissionFoster(token, userId)

    override fun detailSubmissionFoster(
        token: String,
        reqId: Int
    ): Flow<Resource<DetailSubmissionFoster>> = mainRepository.detailSubmissionFoster(token, reqId)

    override fun updateAdopt(
        token: String,
        petId: Int,
        isAdopt: Boolean
    ): Flow<Resource<PetAdopt>> = mainRepository.updateAdopt(token, petId, isAdopt)

    override fun updateStatusReq(
        token: String,
        reqId: Int,
        statusReq: Int
    ): Flow<Resource<FormDetail>> = mainRepository.updateStatusReq(token, reqId, statusReq)

    override fun updatePaymentAdopt(
        token: String,
        reqId: Int,
        data: FormItem
    ): Flow<Resource<FormDetail>> = mainRepository.updatePaymentAdopt(token, reqId, data)

    override fun updateStatusPaymentFoster(
        token: String,
        reqId: Int,
        statusPayment: Int
    ): Flow<Resource<FormDetail>> = mainRepository.updateStatusPaymentFoster(token, reqId, statusPayment)

    override fun updateStatusPickup(
        token: String,
        reqId: Int,
        data: FormItem
    ): Flow<Resource<FormDetail>> = mainRepository.updateStatusPickup(token, reqId, data)

    override fun cancelAdoptUser(
        token: String,
        reqId: Int,
        statusReqId: Int
    ): Flow<Resource<FormDetail>> = mainRepository.cancelAdoptUser(token, reqId, statusReqId)

    override fun getFavoritePets(): Flow<Resource<List<PetEntity>>> = mainRepository.getFavoritePets()

    override suspend fun insertToFavorite(
        petsEntity: PetEntity,
        favoriteState: Boolean
    ): Flow<Resource<PetEntity>> = mainRepository.insertToFavorite(petsEntity, favoriteState)

    override suspend fun deleteFromFavorite(
        petsEntity: PetEntity,
        favoriteState: Boolean
    ): Flow<Resource<PetEntity>> = mainRepository.deleteFromFavorite(petsEntity, favoriteState)

    override suspend fun getFormDetail(token: String, reqId: Int): Flow<Resource<FormDetail>> = mainRepository.getFormDetail(token, reqId)
}