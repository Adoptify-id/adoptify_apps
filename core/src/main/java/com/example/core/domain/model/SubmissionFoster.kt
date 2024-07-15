package com.example.core.domain.model

data class SubmissionFoster(
	val msg: String? = null,
	val data: List<DataSubmissionFoster>,
	val status: Int? = null
)

data class DataSubmissionFoster(
	val foto: String? = null,
	val ras: String? = null,
	val namePet: String? = null,
	val fullName: String? = null,
	val kategori: String? = null,
	val userId: Int? = null,
	val reqId: Int? = null,
	val name: String? = null,
	val statusReqId: Int? = null,
	val statusPaymentId: Int? = null,
	val statusPickupId: Int? = null,
)

