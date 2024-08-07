package com.example.core.domain.model

import com.google.gson.annotations.SerializedName

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
    val fotoPet: String?,

    @field:SerializedName("beratPet")
    val beratPet: Float,

    @field:SerializedName("userId")
    val user_id: Int? = null
)
