package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName
import java.io.File

data class VirtualPetResponse(
    @field:SerializedName("data")
    val virtualPet: List<VirtualPetItem>,
)

data class VirtualPetItem(
    @field:SerializedName("vPetId")
    val virtual_pet_id: Int,

    @field:SerializedName("namePet")
    val name_pet: String,

    @field:SerializedName("umur")
    val umur: Int,

    @field:SerializedName("gender")
    val gender: String,

    @field:SerializedName("ras")
    val ras_pet: String,

    @field:SerializedName("kategori")
    val category: String,

    @field:SerializedName("fotoPet")
    val photo_pet: String,

    @field:SerializedName("beratPet")
    val beratPet: Int,

    @field:SerializedName("userId")
    val user_id: Int? = null
)
