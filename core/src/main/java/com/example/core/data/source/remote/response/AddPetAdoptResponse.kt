package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class AddPetAdoptResponse(

	@field:SerializedName("fotoPetUrl")
	val fotoPetUrl: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
