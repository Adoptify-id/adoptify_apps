package com.example.core.domain.model

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import java.io.File

data class AddVirtualPet(
    val message: String
)

data class AddVirtualPetItem(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("umur")
    val umur: Int,

    @field:SerializedName("gender")
    val gender: String,

    @field:SerializedName("ras")
    val ras: String,

    @field:SerializedName("kategori")
    val kategori: String,

    @field:SerializedName("fotoPet")
    val fotoPet: File?,

    @field:SerializedName("beratPet")
    val beratPet: Int,

    @field:SerializedName("userId")
    val user_id: Int? = null
)
