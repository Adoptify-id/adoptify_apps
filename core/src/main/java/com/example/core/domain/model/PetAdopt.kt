package com.example.core.domain.model

import java.io.File

data class PetAdopt(
    val msg: String? = null,
    val data: List<DataAdopt?>? = null,
    val status: Int? = null

)

data class DataAdopt(
    val fotoPet: String? = null,
    val petId: Int? = null,
    val umur: Int,
    val gender: String,
    val ras: String,
    val descPet: String,
    val kategori: String? = null,
    val namePet: String,
    val isAdopt: Boolean? = false,
    val updateAt: String? = null,
    val createdAt: String? = null,
    val userId: Int? = null,
    val fullName: String? = null,
    val username: String? = null
)
