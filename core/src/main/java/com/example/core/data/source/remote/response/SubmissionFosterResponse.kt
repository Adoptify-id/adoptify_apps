package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SubmissionFosterResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("data")
	val data: List<ItemSubmissionFoster>,

	@field:SerializedName("status")
	val status: Int
)

data class ItemSubmissionFoster(

	@field:SerializedName("foto")
	val foto: String,

	@field:SerializedName("ras")
	val ras: String,

	@field:SerializedName("namePet")
	val namePet: String,

	@field:SerializedName("fullName")
	val fullName: String,

	@field:SerializedName("kategori")
	val kategori: String,

	@field:SerializedName("userId")
	val userId: Int,

	@field:SerializedName("reqId")
	val reqId: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("statusReqId")
	val statusReqId: Int? = null,

	@field:SerializedName("statusPaymentId")
	val statusPaymentId: Int? = null,

	@field:SerializedName("statusPickupId")
	val statusPickupId: Int? = null,
)
