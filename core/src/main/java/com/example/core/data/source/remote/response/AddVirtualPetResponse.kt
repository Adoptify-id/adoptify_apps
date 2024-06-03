package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class AddVirtualPetResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("fotoVpetUrl")
    val photo: String
)