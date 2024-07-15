package com.example.core.domain.model

data class SubmissionPet(
	val msg: String? = null,
	val data: List<SubmissionItem?>? = null,
	val status: Int? = null
)

data class SubmissionItem(
	val ras: String? = null,
	val namePet: String? = null,
	val fullName: String? = null,
	val kategori: Any? = null,
	val userId: Int? = null,
	val reqId: Int? = null,
	val fotoPet: String? = null,
	val statusReqId: Int? = null,
	val statusPaymentId: Int? = null,
	val statusPickupId: Int? = null,
)

