package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class DetailSubmissionResponse(

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("data")
    val data: List<DetailSubmissionItem>,

    @field:SerializedName("status")
    val status: Int
)

data class DetailSubmissionItem(

    @field:SerializedName("umur")
    val umur: Int,

    @field:SerializedName("gender")
    val gender: String,

    @field:SerializedName("descPet")
    val descPet: String,

    @field:SerializedName("ras")
    val ras: String,

    @field:SerializedName("kodePengajuan")
    val kodePengajuan: String,

    @field:SerializedName("kartuIdentitas")
    val kartuIdentitas: String,

    @field:SerializedName("reqId")
    val reqId: Int,

    @field:SerializedName("reqDate")
    val reqDate: String,

    @field:SerializedName("medsos")
    val medsos: String,

    @field:SerializedName("namePet")
    val namePet: String,

    @field:SerializedName("fullName")
    val fullName: String,

    @field:SerializedName("noTelp")
    val noTelp: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("fotoPet")
    val fotoPet: String? = null,

	@field:SerializedName("foto")
	val foto: String? = null,

    @field:SerializedName("statusReqId")
    val statusReqId: Int? = null,

    @field:SerializedName("statusPaymentId")
    val statusPaymentId: Int? = null,

    @field:SerializedName("statusPickupId")
    val statusPickupId: Int? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("provinsi")
	val provinsi: String? = null,

	@field:SerializedName("kodePos")
	val kodePos: String? = null,

    @field:SerializedName("buktiPickup")
    val buktiPickup: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("suratKomitmen")
    val suratKomitmen: String? = null,

    @field:SerializedName("transfer")
    val transfer: String? = null,

    @field:SerializedName("kategori")
    val kategori: String? = null,
)
