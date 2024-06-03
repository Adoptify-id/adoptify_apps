package com.example.core.utils

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
import com.example.core.domain.model.AddVaksinasiPet
import com.example.core.domain.model.AddVirtualPet
import com.example.core.domain.model.AddVirtualPetItem
import com.example.core.domain.model.DataAdopt
import com.example.core.domain.model.DetailDataUser
import com.example.core.domain.model.DetailUser
import com.example.core.domain.model.Login
import com.example.core.domain.model.MedicalItem
import com.example.core.domain.model.MedicalRecord
import com.example.core.domain.model.PetAdopt
import com.example.core.domain.model.Register
import com.example.core.domain.model.User
import com.example.core.domain.model.VaksinasiData
import com.example.core.domain.model.VirtualPetItem
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataMapper {
    fun loginResponseToLogin(data: LoginResponse): Login = Login(
        accessToken = data.accessToken,
        username = data.username,
        userId = data.userId,
        roleId = data.roleId
    )

    fun registerResponseToRegister(data: RegisterResponse): Register = Register(data.message)

    fun userMap(user: User) = User(user.username, user.email, user.phone, user.password)

    fun virtualPetItemToVirtualPet(data: List<com.example.core.data.source.remote.response.VirtualPetItem>): List<VirtualPetItem> =
        data.map {
            VirtualPetItem(
                virtual_pet_id = it.virtual_pet_id,
                name_pet = it.name_pet,
                umur = it.umur,
                gender = it.gender,
                ras_pet = it.ras_pet,
                category = it.category,
                photo_pet = it.photo_pet,
                berat_pet = it.beratPet,
                user_id = it.user_id
            )
        }

    fun virtualPetMap(data: AddVirtualPetItem): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["name"] = data.name.toRequestBody()
        requestBodyMap["umur"] = data.umur.toString().toRequestBody()
        requestBodyMap["gender"] = data.gender.toRequestBody()
        requestBodyMap["ras"] = data.ras.toRequestBody()
        requestBodyMap["kategori"] = data.kategori.toRequestBody()
        requestBodyMap["beratPet"] = data.beratPet.toString().toRequestBody()
        requestBodyMap["userId"] = data.user_id.toString().toRequestBody()

        val file = data.fotoPet ?: return requestBodyMap

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        requestBodyMap["fotoPet\"; filename=\"${file.name}\""] = requestFile

        return requestBodyMap
    }

    fun virtualPetResponseToVirtualPet(data: AddVirtualPetResponse): AddVirtualPet =
        AddVirtualPet(data.message)

    fun vakasinasiItemToVaksinasi(data: List<VaksinasiItem>): List<VaksinasiData> =
        data.map {
            VaksinasiData(
                vaksinId = it.vaksinId,
                kategoriPet = it.kategoriPet,
                name = it.name,
                kesehatan = it.kesehatan,
                descKesehatan = it.descKesehatan,
                beratPet = it.beratPet,
                info = it.info,
                klinikName = it.klinikName,
                dokterName = it.dokterName,
                alamat = it.alamat,
                tanggal = it.tanggal,
                jenisVaksin = it.jenisVaksin,
                catatan = it.catatan,
                created_at = it.created_at,
                userId = it.userId
            )
        }

    fun vaksinasiMap(data: VaksinasiItem) = VaksinasiData(
        vaksinId = data.vaksinId,
        kategoriPet = data.kategoriPet,
        name = data.name,
        kesehatan = data.kesehatan,
        descKesehatan = data.descKesehatan,
        beratPet = data.beratPet,
        info = data.info,
        klinikName = data.klinikName,
        dokterName = data.dokterName,
        alamat = data.alamat,
        tanggal = data.tanggal,
        jenisVaksin = data.jenisVaksin,
        catatan = data.catatan,
        userId = data.userId
    )

    fun vaksinasiResponseToVaksinasi(data: AddVaksinasiResponse): AddVaksinasiPet = AddVaksinasiPet(data.message)

    fun updateUserMapping(data: DataUser) : Map<String, RequestBody>  {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["gender"] = data.gender!!.toRequestBody()
        requestBodyMap["provinsi"] = data.provinsi!!.toRequestBody()
        requestBodyMap["alamat"] = data.alamat!!.toRequestBody()
        requestBodyMap["tglLahir"] = data.tglLahir!!.toRequestBody()
        requestBodyMap["kodePos"] = data.kodePos!!.toRequestBody()
        requestBodyMap["fullName"] = data.fullName!!.toRequestBody()
        requestBodyMap["userId"] = data.userId!!.toString().toRequestBody()

        val file = data.foto ?: return requestBodyMap
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        requestBodyMap["foto\"; filename=\"${file}\""] = requestFile


        return requestBodyMap
    }
    fun updateUserResponseToUser(data: UserResponse) : DetailUser = DetailUser(data.msg)

    fun detailDataUserResponseToUser(data: UserResponse) : DetailUser = DetailUser(
        msg = data.msg,
        data = data.data?.map {
            DetailDataUser(
                provinsi = it?.provinsi,
                gender = it?.gender,
                noTelp = it?.noTelp,
                alamat = it?.alamat,
                tglLahir = it?.tglLahir,
                kodePos = it?.kodePos,
                email = it?.email,
                username = it?.username,
                fullName = it?.fullName,
                foto = it?.foto
            )
        }
    )

    fun medicalItemToMedical(data: List<DataItem>) : List<MedicalItem> =
        data.map {
            MedicalItem(
                beratPet = it.beratPet,
                kesehatan = it.kesehatan,
                xRay = it.xRay,
                catatan = it.catatan,
                createdAt = it.createdAt,
                alamat = it.alamat,
                descKesehatan = it.descKesehatan,
                namePet = it.namePet,
                tanggal = it.tanggal,
                klinikName = it.klinikName,
                dokterName = it.dokterName,
                kategoriPet = it.kategoriPet,
                info = it.info
            )
        }

    fun medicalItemMap(data: DataItem): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["kategoriPet"] = data.kategoriPet.toRequestBody()
        requestBodyMap["name"] = data.namePet.toRequestBody()
        requestBodyMap["kesehatan"] = data.kesehatan.toRequestBody()
        requestBodyMap["descKesehatan"] = data.descKesehatan.toRequestBody()
        requestBodyMap["beratPet"] = data.beratPet.toString().toRequestBody()
        requestBodyMap["info"] = data.info.toRequestBody()
        requestBodyMap["klinikName"] = data.klinikName.toRequestBody()
        requestBodyMap["dokterName"] = data.dokterName.toRequestBody()
        requestBodyMap["alamat"] = data.alamat.toRequestBody()
        requestBodyMap["tanggal"] = data.tanggal.toRequestBody()
        requestBodyMap["catatan"] = data.catatan.toRequestBody()
        requestBodyMap["userId"] = data.userId.toString().toRequestBody()

        val file = data.xRay ?: return requestBodyMap

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        requestBodyMap["xRay\"; filename=\"${file}\""] = requestFile

        return requestBodyMap
    }

    fun medicalResponseToMedical(data: AddMedicalResponse) : MedicalRecord = MedicalRecord(data.message)

    fun petAdoptItemMap(data: PetAdoptItem) : Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["namePet"] = data.namePet.toRequestBody()
        requestBodyMap["umur"] = data.umur.toString().toRequestBody()
        requestBodyMap["gender"] = data.gender.toRequestBody()
        requestBodyMap["ras"] = data.ras.toRequestBody()
        requestBodyMap["descPet"] = data.descPet.toRequestBody()
        requestBodyMap["kategori"] = data.kategori!!.toRequestBody()
        requestBodyMap["userId"] = data.userId.toString().toRequestBody()

        val file = data.fotoPet ?: return requestBodyMap

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        requestBodyMap["fotoPet\"; filename=\"${file}\""] = requestFile

        return requestBodyMap
    }

    fun petResponseToPet(data: AddPetAdoptResponse) : PetAdopt = PetAdopt(data.message)

    fun petAdoptItemToPet(data: List<PetAdoptItem>) : List<DataAdopt> =
        data.map {
            DataAdopt(
                fotoPet = it.fotoPet,
                petId = it.petId,
                umur = it.umur,
                gender = it.gender,
                ras = it.ras,
                descPet = it.descPet,
                kategori = it.kategori,
                namePet = it.namePet,
                createdAt = it.createdAt,
                userId = it.userId,

            )
        }

    fun detailDataPetToPet(data: PetAdoptResponse) : PetAdopt = PetAdopt(
        msg = data.msg,
        data = data.data.map {
            DataAdopt(
                petId = it.petId,
                fotoPet = it.fotoPet,
                umur = it.umur,
                gender = it.gender,
                ras = it.ras,
                descPet = it.descPet,
                kategori = it.kategori,
                namePet = it.namePet,
                fullName = it.fullName,
                username = it.username
            )
        }
    )
}