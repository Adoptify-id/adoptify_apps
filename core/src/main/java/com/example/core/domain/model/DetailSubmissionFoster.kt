package com.example.core.domain.model

data class DetailSubmissionFoster(
	val msg: String,
	val data: List<DetailItemSubmission>,
	val status: Int
)

data class DetailItemSubmission(
	val umur: Int,
	val gender: String,
	val descPet: String,
	val ras: String,
	val kodePengajuan: String,
	val kartuIdentitas: String,
	val reqId: Int,
	val reqDate: String,
	val medsos: String? = null,
	val namePet: String,
	val name: String,
	val noWa: String,
	val domisili: String,
	val email: String,
	val petId: Int,
	val foto: String? = null,
	val fotoPet: String? = null,
	val statusReqId: Int? = null,
	val statusPaymentId: Int? = null,
	val statusPickupId: Int? = null,
	val suratKomitmen: String? = null,
	val transfer: String? = null,
	val buktiPickup: String? = null,
	val kategori: String? = null
)

