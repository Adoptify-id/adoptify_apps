package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class VaksinasiResponse(
    @field:SerializedName("msg")
    val message: String,

    @field:SerializedName("status")
    val status: Int,

    @field:SerializedName("data")
    val data: List<VaksinasiItem>
)

data class VaksinasiItem(
    @field:SerializedName("vaksinId")
    val vaksinId: Int? = null,

    @field:SerializedName("kategoriPet")
    val kategoriPet: String,

    @field:SerializedName("namePet")
    val name: String,

    @field:SerializedName("kesehatan")
    val kesehatan: String,

    @field:SerializedName("descKesehatan")
    val descKesehatan: String,

    @field:SerializedName("beratPet")
    val beratPet: Float,

    @field:SerializedName("info")
    val info: String,

    @field:SerializedName("klinikName")
    val klinikName: String,

    @field:SerializedName("dokterName")
    val dokterName: String,

    @field:SerializedName("alamat")
    val alamat: String,

    @field:SerializedName("tanggal")
    val tanggal: String,

    @field:SerializedName("jenisVaksin")
    val jenisVaksin: String,

    @field:SerializedName("catatan")
    val catatan: String,

    @field:SerializedName("created_at")
    val created_at: String? = null,

    @field:SerializedName("userId")
    val userId: Int,
)
