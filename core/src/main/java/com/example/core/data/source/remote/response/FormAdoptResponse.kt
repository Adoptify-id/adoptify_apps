package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class FormAdoptResponse(
	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("kartuIdentitasUrl")
	val kartuIdentitasUrl: String? = null
)
