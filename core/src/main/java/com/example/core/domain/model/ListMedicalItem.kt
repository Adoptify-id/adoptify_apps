package com.example.core.domain.model

sealed class ListMedicalItem {
    data class VaksinasiItem(val data: VaksinasiData) : ListMedicalItem()

    data class MedicalData(val data: MedicalItem) : ListMedicalItem()

    data class HeaderItem(val header: String) : ListMedicalItem()
}