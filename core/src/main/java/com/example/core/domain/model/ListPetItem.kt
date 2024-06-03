package com.example.core.domain.model

sealed class ListPetItem {
    data class PetItem(val data: DataAdopt) : ListPetItem()

    data class HeaderItem(val header: String) : ListPetItem()
}