package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class AddMedicalResponse(

	@field:SerializedName("xRayUrl")
	val xRayUrl: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
