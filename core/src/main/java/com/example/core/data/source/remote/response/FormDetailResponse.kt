package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class FormDetailResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("data")
	val data: List<FormItem?>? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class FormItem(
	@field:SerializedName("kodePengajuan")
	val kodePengajuan: String? = null,

	@field:SerializedName("umur")
	val umur: Int? = null,

	@field:SerializedName("ppk1")
	val ppk1: String? = null,

	@field:SerializedName("ppk2")
	val ppk2: String? = null,

	@field:SerializedName("ppk10")
	val ppk10: String? = null,

	@field:SerializedName("rangePendapatan")
	val rangePendapatan: String? = null,

	@field:SerializedName("noWa")
	val noWa: String? = null,

	@field:SerializedName("ppk7")
	val ppk7: String? = null,

	@field:SerializedName("ppk8")
	val ppk8: String? = null,

	@field:SerializedName("ppk9")
	val ppk9: String? = null,

	@field:SerializedName("ppk3")
	val ppk3: String? = null,

	@field:SerializedName("ppk4")
	val ppk4: String? = null,

	@field:SerializedName("ppk5")
	val ppk5: String? = null,

	@field:SerializedName("ppk6")
	val ppk6: String? = null,

	@field:SerializedName("umum4")
	val umum4: String? = null,

	@field:SerializedName("umum5")
	val umum5: String? = null,

	@field:SerializedName("umum6")
	val umum6: String? = null,

	@field:SerializedName("umum1")
	val umum1: String? = null,

	@field:SerializedName("umum2")
	val umum2: String? = null,

	@field:SerializedName("umum3")
	val umum3: String? = null,

	@field:SerializedName("reqId")
	val reqId: Int? = null,

	@field:SerializedName("pekerjaan")
	val pekerjaan: String? = null,

	@field:SerializedName("medsos")
	val medsos: String? = null,

	@field:SerializedName("rl1")
	val rl1: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("rl3")
	val rl3: String? = null,

	@field:SerializedName("rl2")
	val rl2: String? = null,

	@field:SerializedName("rl5")
	val rl5: String? = null,

	@field:SerializedName("rl4")
	val rl4: String? = null,

	@field:SerializedName("domisili")
	val domisili: String? = null,

	@field:SerializedName("kartuIdentitas")
	val kartuIdentitas: String? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("petId")
	val petId: Int? = null,

	@field:SerializedName("statusReqId")
	val statusReqId: Int? = null,

	@field:SerializedName("statusPaymentId")
	val statusPaymentId: Int? = null,

	@field:SerializedName("statusPickupId")
	val statusPickupId: Int? = null,

	@field:SerializedName("reqDate")
	val reqDate: String? = null,

	@field:SerializedName("buktiPickup")
	val buktiPickup: String? = null,

	@field:SerializedName("suratKomitmen")
	val suratKomitmen: String? = null,

	@field:SerializedName("transfer")
	val transfer: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null
)
