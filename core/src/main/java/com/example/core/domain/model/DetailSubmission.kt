package com.example.core.domain.model

data class DetailSubmission(
	val msg: String? = null,
	val data: List<DetailSubmissionData>,
	val status: Int? = null
)

data class DetailSubmissionData(
	val umur: Int? = null,
	val gender: String? = null,
	val descPet: String? = null,
	val ras: String? = null,
	val kodePengajuan: String? = null,
	val kartuIdentitas: String? = null,
	val reqId: Int? = null,
	val reqDate: String? = null,
	val medsos: String? = null,
	val namePet: String? = null,
	val fullName: String? = null,
	val noTelp: String? = null,
	val alamat: String? = null,
	val provinsi: String? = null,
	val kodePos: String? = null,
	val email: String? = null,
	val fotoPet: String? = null,
	val foto: String? = null,
	val statusReqId: Int? = null,
	val statusPaymentId: Int? = null,
	val statusPickupId: Int? = null,
	val kategori: String? = null,
	val buktiPickup: String? = null
)

