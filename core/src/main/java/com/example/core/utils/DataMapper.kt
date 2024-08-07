package com.example.core.utils

import com.example.core.data.source.local.entity.PetEntity
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
import com.example.core.domain.model.AddVaksinasiPet
import com.example.core.domain.model.AddVirtualPet
import com.example.core.domain.model.AddVirtualPetItem
import com.example.core.domain.model.DataAdopt
import com.example.core.domain.model.DataSubmissionFoster
import com.example.core.domain.model.DetailDataUser
import com.example.core.domain.model.DetailItemSubmission
import com.example.core.domain.model.DetailSubmission
import com.example.core.domain.model.DetailSubmissionData
import com.example.core.domain.model.DetailSubmissionFoster
import com.example.core.domain.model.DetailUser
import com.example.core.domain.model.FormData
import com.example.core.domain.model.FormDetail
import com.example.core.domain.model.Login
import com.example.core.domain.model.MedicalItem
import com.example.core.domain.model.MedicalRecord
import com.example.core.domain.model.PetAdopt
import com.example.core.domain.model.Register
import com.example.core.domain.model.SubmissionItem
import com.example.core.domain.model.User
import com.example.core.domain.model.VaksinasiData
import com.example.core.domain.model.VirtualPetItem
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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


        data.fotoPet?.let {
            val file = File(it)
            if (file.exists()) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                requestBodyMap["fotoPet\"; filename=\"${file.name}"] = requestFile
            }
        }

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

    fun vaksinasiResponseToVaksinasi(data: AddVaksinasiResponse): AddVaksinasiPet =
        AddVaksinasiPet(data.message)

    fun updateUserMapping(data: DataUser): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["gender"] = data.gender!!.toRequestBody()
        requestBodyMap["provinsi"] = data.provinsi!!.toRequestBody()
        requestBodyMap["alamat"] = data.alamat!!.toRequestBody()
        requestBodyMap["tglLahir"] = data.tglLahir!!.toRequestBody()
        requestBodyMap["kodePos"] = data.kodePos!!.toRequestBody()
        requestBodyMap["fullName"] = data.fullName!!.toRequestBody()
        requestBodyMap["userId"] = data.userId!!.toString().toRequestBody()

        data.foto?.let {
            val file = File(it)
            if (file.exists()) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                requestBodyMap["foto\"; filename=\"${file.name}"] = requestFile
            }
        }
        return requestBodyMap
    }

    fun updateUserResponseToUser(data: UserResponse): DetailUser = DetailUser(data.msg)

    fun detailDataUserResponseToUser(data: UserResponse): DetailUser = DetailUser(
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

    fun medicalItemToMedical(data: List<DataItem>): List<MedicalItem> =
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

        data.xRay?.let {
            val file = File(it)
            if (file.exists()) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                requestBodyMap["xRay\"; filename=\"${file.name}"] = requestFile
            }
        }

        return requestBodyMap
    }

    fun medicalResponseToMedical(data: AddMedicalResponse): MedicalRecord =
        MedicalRecord(data.message)

    fun petAdoptItemMap(data: PetAdoptItem): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["namePet"] = data.namePet!!.toRequestBody()
        requestBodyMap["umur"] = data.umur!!.toString().toRequestBody()
        requestBodyMap["gender"] = data.gender!!.toRequestBody()
        requestBodyMap["ras"] = data.ras!!.toRequestBody()
        requestBodyMap["descPet"] = data.descPet!!.toRequestBody()
        requestBodyMap["kategori"] = data.kategori!!.toRequestBody()
        requestBodyMap["userId"] = data.userId.toString().toRequestBody()
        requestBodyMap["ageType"] = data.ageType!!.toRequestBody()

        data.fotoPet?.let {
            val file = File(it)
            if (file.exists()) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                requestBodyMap["fotoPet\"; filename=\"${file.name}"] = requestFile
            }
        }

        return requestBodyMap
    }

    fun petResponseToPet(data: AddPetAdoptResponse): PetAdopt = PetAdopt(data.message)

    fun petAdoptItemToPet(data: List<PetAdoptItem>): List<DataAdopt> =
        data.map {
            DataAdopt(
                fotoPet = it.fotoPet,
                petId = it.petId,
                umur = it.umur,
                gender = it.gender,
                ras = it.ras,
                descPet = it.descPet,
                kategori = it.kategori,
                isAdopt = it.isAdopt,
                namePet = it.namePet,
                createdAt = it.createdAt,
                userId = it.userId,
                alamat = it.alamat,
                provinsi = it.provinsi,
                ageType = it.ageType
            )
        }

    fun dataAdoptToPetEntity(data: List<DataAdopt>, isFavorite: Boolean): List<PetEntity> =
        data.map {
            PetEntity(
                id = it.petId!!,
                namePet = it.namePet!!,
                gender = it.gender!!,
                age = it.umur!!,
                ras = it.ras!!,
                fotoPet = it.fotoPet!!,
                isAdopt = it.isAdopt,
                isFavorite = isFavorite,
                address = it.alamat,
                province = it.provinsi,
                ageType = it.ageType
            )
        }


    fun detailDataPetToPet(data: PetAdoptResponse): PetAdopt = PetAdopt(
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
                username = it.username,
                isAdopt = it.isAdopt,
                alamat = it.alamat,
                provinsi = it.provinsi,
                foto = it.foto,
                ageType = it.ageType
            )
        }
    )

    fun formItemAdoptMap(data: FormItem): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["name"] = data.name!!.toRequestBody()
        requestBodyMap["umur"] = data.umur!!.toString().toRequestBody()
        requestBodyMap["noWa"] = data.noWa!!.toRequestBody()
        requestBodyMap["domisili"] = data.domisili!!.toRequestBody()
        requestBodyMap["pekerjaan"] = data.pekerjaan!!.toRequestBody()
        requestBodyMap["rangePendapatan"] = data.rangePendapatan!!.toRequestBody()
        requestBodyMap["medsos"] = data.medsos!!.toRequestBody()

        data.kartuIdentitas?.let {
            val file = File(it)
            if (file.exists()) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                requestBodyMap["kartuIdentitas\"; filename=\"${file.name}"] = requestFile
            }
        }

        requestBodyMap["umum1"] = data.umum1!!.toRequestBody()
        requestBodyMap["umum2"] = data.umum2!!.toRequestBody()
        requestBodyMap["umum3"] = data.umum3!!.toRequestBody()
        requestBodyMap["umum4"] = data.umum4!!.toRequestBody()
        requestBodyMap["umum5"] = data.umum5!!.toRequestBody()
        requestBodyMap["umum6"] = data.umum6!!.toRequestBody()
        requestBodyMap["ppk1"] = data.ppk1!!.toRequestBody()
        requestBodyMap["ppk2"] = data.ppk2!!.toRequestBody()
        requestBodyMap["ppk3"] = data.ppk3!!.toRequestBody()
        requestBodyMap["ppk4"] = data.ppk4!!.toRequestBody()
        requestBodyMap["ppk5"] = data.ppk5!!.toRequestBody()
        requestBodyMap["ppk6"] = data.ppk6!!.toRequestBody()
        requestBodyMap["ppk7"] = data.ppk7!!.toRequestBody()
        requestBodyMap["ppk8"] = data.ppk8!!.toRequestBody()
        requestBodyMap["ppk9"] = data.ppk9!!.toRequestBody()
        requestBodyMap["ppk10"] = data.ppk10!!.toRequestBody()
        requestBodyMap["rl1"] = data.rl1!!.toRequestBody()
        requestBodyMap["rl2"] = data.rl2!!.toRequestBody()
        requestBodyMap["rl3"] = data.rl3!!.toRequestBody()
        requestBodyMap["rl4"] = data.rl4!!.toRequestBody()
        requestBodyMap["rl5"] = data.rl5!!.toRequestBody()
        requestBodyMap["userId"] = data.userId.toString().toRequestBody()
        requestBodyMap["petId"] = data.petId.toString().toRequestBody()

        return requestBodyMap
    }

    fun formResponseToForm(data: FormAdoptResponse): FormDetail = FormDetail(data.message)

    fun submissionListPet(data: List<DataSubmission>): List<SubmissionItem> =
        data.map {
            SubmissionItem(
                ras = it.ras,
                namePet = it.namePet,
                kategori = it.kategori,
                reqId = it.reqId,
                fullName = it.fullName,
                fotoPet = it.fotoPet,
                statusPaymentId = it.statusPaymentId,
                statusPickupId = it.statusPickupId,
                statusReqId = it.statusReqId
            )
        }

    fun detailSubmissionToSubmission(data: DetailSubmissionResponse): DetailSubmission =
        DetailSubmission(
            msg = data.msg,
            data = data.data.map {
                DetailSubmissionData(
                    reqId = it.reqId,
                    umur = it.umur,
                    gender = it.gender,
                    descPet = it.descPet,
                    ras = it.ras,
                    kodePengajuan = it.kodePengajuan,
                    kartuIdentitas = it.kartuIdentitas,
                    reqDate = it.reqDate,
                    namePet = it.namePet,
                    fullName = it.fullName,
                    noTelp = it.noTelp,
                    alamat = it.alamat,
                    provinsi = it.provinsi,
                    kodePos = it.kodePos,
                    foto = it.foto,
                    email = it.email,
                    fotoPet = it.fotoPet,
                    statusPaymentId = it.statusPaymentId,
                    statusPickupId = it.statusPickupId,
                    statusReqId = it.statusReqId,
                    buktiPickup = it.buktiPickup,
                    name = it.name,
                    kategori = it.kategori,
                    suratKomitmen = it.suratKomitmen,
                    transfer = it.transfer,
                )
            }
        )

    fun submissionListFoster(data: List<ItemSubmissionFoster>): List<DataSubmissionFoster> =
        data.map {
            DataSubmissionFoster(
                ras = it.ras,
                namePet = it.namePet,
                kategori = it.kategori,
                reqId = it.reqId,
                foto = it.foto,
                name = it.name,
                statusPaymentId = it.statusPaymentId,
                statusPickupId = it.statusPickupId,
                statusReqId = it.statusReqId
            )
        }

    fun detailSubmissionFosterToSubmissionFoster(data: DetailSubmissionFosterResponse): DetailSubmissionFoster =
        DetailSubmissionFoster(
            msg = data.msg,
            status = data.status,
            data = data.data.map {
                DetailItemSubmission(
                    umur = it.umur,
                    gender = it.gender,
                    descPet = it.descPet,
                    ras = it.ras,
                    kodePengajuan = it.kodePengajuan,
                    kartuIdentitas = it.kartuIdentitas,
                    suratKomitmen = it.suratKomitmen,
                    transfer = it.transfer,
                    buktiPickup = it.buktiPickup,
                    reqId = it.reqId,
                    reqDate = it.reqDate,
                    namePet = it.namePet,
                    name = it.name,
                    noWa = it.noWa,
                    domisili = it.domisili,
                    email = it.email,
                    foto = it.foto,
                    fotoPet = it.fotoPet,
                    statusPaymentId = it.statusPaymentId,
                    statusPickupId = it.statusPickupId,
                    statusReqId = it.statusReqId,
                    petId = it.petId,
                    kategori = it.kategori
                )
            }
        )

    fun updateFormResponseToForm(data: FormDetailResponse): FormDetail = FormDetail(
        msg = data.msg,
        data = data.data?.map {
            FormData(
                statusReqId = it?.statusReqId,
                statusPickupId = it?.statusPickupId,
                statusPaymentId = it?.statusPaymentId
            )
        }
    )

    fun updateFormPayment(data: FormItem): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["statusPaymentId"] = data.statusPaymentId!!.toString().toRequestBody()
        data.suratKomitmen?.let {
            val file = File(it)
            if (file.exists()) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                requestBodyMap["suratKomitmen\"; filename=\"${file.name}"] = requestFile
            }
        }
        data.transfer?.let {
            val file = File(it)
            if (file.exists()) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                requestBodyMap["transfer\"; filename=\"${file.name}"] = requestFile
            }
        }

        return requestBodyMap
    }

    fun updatePickup(data: FormItem): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        requestBodyMap["statusPickupId"] = data.statusPickupId!!.toString().toRequestBody()
        data.buktiPickup?.let {
            val file = File(it)
            if (file.exists()) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                requestBodyMap["buktiPickup\"; filename=\"${file.name}"] = requestFile
            }
        }
        return requestBodyMap
    }

    fun petsEntityToPets(pet: DataAdopt, isFavorite: Boolean): PetEntity {
        return PetEntity(
            id = pet.petId!!,
            namePet = pet.namePet!!,
            gender = pet.gender!!,
            age = pet.umur!!,
            ras = pet.ras!!,
            fotoPet = pet.fotoPet!!,
            isAdopt = pet.isAdopt,
            isFavorite = isFavorite,
            address = pet.alamat,
            province = pet.provinsi,
            ageType = pet.ageType
        )
    }

    fun formDetailResponseToForm(data: FormDetailResponse): FormDetail = FormDetail(
        msg = data.msg,
        data = data.data?.map {
            FormData(
                reqId = it?.reqId,
                name = it?.name,
                umur = it?.umur,
                domisili = it?.domisili,
                noWa = it?.noWa,
                pekerjaan = it?.pekerjaan,
                rangePendapatan = it?.rangePendapatan,
                medsos = it?.medsos,
                kartuIdentitas = it?.kartuIdentitas,
                ppk1 =  it?.ppk1,
                ppk2 =  it?.ppk2,
                ppk3 =  it?.ppk3,
                ppk4 =  it?.ppk4,
                ppk5 =  it?.ppk5,
                ppk6 =  it?.ppk6,
                ppk7 =  it?.ppk7,
                ppk8 =  it?.ppk8,
                ppk9 =  it?.ppk9,
                ppk10 =  it?.ppk10,
                umum1 = it?.umum1,
                umum2 = it?.umum2,
                umum3 = it?.umum3,
                umum4 = it?.umum4,
                umum5 = it?.umum5,
                umum6 = it?.umum6,
                rl1 = it?.rl1,
                rl2 = it?.rl2,
                rl3 = it?.rl3,
                rl4 = it?.rl4,
                rl5 = it?.rl5,
                kategori = it?.kategori
            )
        }
    )


    fun parseErrorMessage(errorBody: String?): String {
        return if (errorBody != null) {

            try {
                val json = Gson().fromJson(errorBody, JsonObject::class.java)
                json.get("detail").asString
            } catch (e: Exception) {
                "Error parsing response"
            }
        } else {
            "Unknown error"
        }
    }
}