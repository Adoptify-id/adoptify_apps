package com.example.core.domain.model

data class PetAdopt(
    val msg: String? = null,
    val data: List<DataAdopt?>? = null,
    val status: Int? = null
)

data class DataAdopt(
    val fotoPet: String? = null,
    val petId: Int? = null,
    val umur: Int? = null,
    val gender: String? = null,
    val ras: String? = null,
    val descPet: String? = null,
    val kategori: String? = null,
    val namePet: String? = null,
    val isAdopt: Boolean? = false,
    val updateAt: String? = null,
    val createdAt: String? = null,
    val userId: Int? = null,
    val fullName: String? = null,
    val username: String? = null,
    val alamat: String? = null,
    val provinsi: String? = null,
    val foto: String? = null,
)
