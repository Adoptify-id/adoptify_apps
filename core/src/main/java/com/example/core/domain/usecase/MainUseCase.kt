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
import kotlinx.coroutines.flow.Flow

interface MainUseCase {
    fun getVirtualPet(token: String, userId: Int): Flow<Resource<List<VirtualPetItem>>>

    fun insertVirtualPet(token:String, data: AddVirtualPetItem) : Flow<Resource<AddVirtualPet>>

    fun getVaksinasi(token: String, userId: Int) : Flow<Resource<List<VaksinasiData>>>

    fun insertVaksinasi(token: String, data: VaksinasiItem): Flow<Resource<AddVaksinasiPet>>

    fun getMedicalRecord(token: String, userId: Int) : Flow<Resource<List<MedicalItem>>>

    fun insertMedicalRecord(token: String, data: DataItem) : Flow<Resource<MedicalRecord>>

    fun insertPetAdopt(token: String, data: PetAdoptItem) : Flow<Resource<PetAdopt>>

    fun getListPet(token: String) : Flow<Resource<List<DataAdopt>>>

    fun getDetailPet(token: String, petId: Int) : Flow<Resource<PetAdopt>>

    fun updatePet(token: String, petId: Int, data: PetAdoptItem) : Flow<Resource<PetAdopt>>

    fun getPetByUser(token: String, userId: Int) : Flow<Resource<List<DataAdopt>>>

    fun formAdopt(token: String, form: FormItem) : Flow<Resource<FormDetail>>

    fun getListSubmissionPet(token: String, userId: Int) : Flow<Resource<List<SubmissionItem>>>

    fun getDetailSubmissionPet(token: String, reqId: Int) : Flow<Resource<DetailSubmission>>

    fun getListSubmissionFoster(token: String, userId: Int) : Flow<Resource<List<DataSubmissionFoster>>>

    fun detailSubmissionFoster(token: String, reqId: Int) : Flow<Resource<DetailSubmissionFoster>>

    fun updateAdopt(token: String, petId: Int, isAdopt: Boolean) : Flow<Resource<PetAdopt>>

    fun updateStatusReq(token: String, reqId: Int, statusReq: Int) : Flow<Resource<FormDetail>>

    fun updatePaymentAdopt(token: String, reqId: Int, data: FormItem) : Flow<Resource<FormDetail>>

    fun updateStatusPaymentFoster(token: String, reqId: Int, statusPayment: Int) : Flow<Resource<FormDetail>>

    fun updateStatusPickup(token: String, reqId: Int, data: FormItem) : Flow<Resource<FormDetail>>

    fun cancelAdoptUser(token: String, reqId: Int, statusReqId: Int) : Flow<Resource<FormDetail>>

    fun getFavoritePets() : Flow<Resource<List<PetEntity>>>

    suspend fun insertToFavorite(petsEntity: PetEntity, favoriteState: Boolean) : Flow<Resource<PetEntity>>

    suspend fun deleteFromFavorite(petsEntity: PetEntity, favoriteState: Boolean) : Flow<Resource<PetEntity>>

    suspend fun getFormDetail(token: String, reqId: Int) : Flow<Resource<FormDetail>>
}
