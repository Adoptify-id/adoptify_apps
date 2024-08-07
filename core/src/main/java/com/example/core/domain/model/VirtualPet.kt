package com.example.core.domain.model

data class VirtualPet(
    val data: List<VirtualPetItem>,
)

data class VirtualPetItem(
    var virtual_pet_id: Int? = null,
    val name_pet: String,
    val umur: Int,
    val gender: String,
    val ras_pet: String,
    val category: String,
    val photo_pet: String,
    val berat_pet: Float,
    val user_id: Int? = null
)

