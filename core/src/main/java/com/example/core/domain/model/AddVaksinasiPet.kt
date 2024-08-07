package com.example.core.domain.model

data class AddVaksinasiPet(
    val message: String,
//    val data: List<VaksinasiData>
)

data class VaksinasiData(
    val vaksinId: Int?,
    val kategoriPet: String,
    val name: String,
    val kesehatan: String,
    val descKesehatan: String,
    val beratPet: Float,
    val info: String,
    val klinikName: String,
    val dokterName: String,
    val alamat: String,
    val tanggal: String,
    val jenisVaksin: String,
    val catatan: String,
    val created_at: String? = null,
    val userId: Int,
)