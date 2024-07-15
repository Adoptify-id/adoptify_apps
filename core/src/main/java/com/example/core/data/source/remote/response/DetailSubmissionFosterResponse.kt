package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class DetailSubmissionFosterResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("data")
	val data: List<DetailDataSubmissionFoster>,

	@field:SerializedName("status")
	val status: Int
)

data class DetailDataSubmissionFoster(

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

	@field:SerializedName("suratKomitmen")
	val suratKomitmen: String? = null,

	@field:SerializedName("transfer")
	val transfer: String? = null,

	@field:SerializedName("buktiPickup")
	val buktiPickup: String? = null,

	@field:SerializedName("reqId")
	val reqId: Int,

	@field:SerializedName("reqDate")
	val reqDate: String,

	@field:SerializedName("medsos")
	val medsos: String,

	@field:SerializedName("namePet")
	val namePet: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("foto")
	val foto: String,

	@field:SerializedName("fotoPet")
	val fotoPet: String,

	@field:SerializedName("noWa")
	val noWa: String,

	@field:SerializedName("domisili")
	val domisili: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("statusReqId")
	val statusReqId: Int? = null,

	@field:SerializedName("statusPaymentId")
	val statusPaymentId: Int? = null,

	@field:SerializedName("statusPickupId")
	val statusPickupId: Int? = null,

	@field:SerializedName("petId")
	val petId: Int,

	@field:SerializedName("kategori")
	val kategori: String? = null,
)
