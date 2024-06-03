package com.example.core.domain.repository

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
import kotlinx.coroutines.flow.Flow

interface IMainRepository {
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
}