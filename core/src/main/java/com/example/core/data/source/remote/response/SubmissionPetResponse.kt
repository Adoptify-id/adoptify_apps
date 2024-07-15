package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SubmissionPetResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("data")
	val data: List<DataSubmission>,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DataSubmission(

	@field:SerializedName("ras")
	val ras: String? = null,

	@field:SerializedName("namePet")
	val namePet: String? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("kategori")
	val kategori: Any? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("reqId")
	val reqId: Int? = null,

	@field:SerializedName("fotoPet")
	val fotoPet: String? = null,

	@field:SerializedName("statusReqId")
	val statusReqId: Int? = null,

	@field:SerializedName("statusPaymentId")
	val statusPaymentId: Int? = null,

	@field:SerializedName("statusPickupId")
	val statusPickupId: Int? = null,
)
