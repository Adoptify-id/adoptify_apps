package com.example.core.domain.usecase

import com.example.core.data.Resource
import com.example.core.data.source.remote.response.DataItem
import com.example.core.data.source.remote.response.DataUser
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.data.source.remote.response.VaksinasiItem
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
import com.example.core.domain.model.VirtualPetItem
import com.example.core.domain.model.VirtualPet
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

}