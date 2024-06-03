package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName
import java.io.File

data class MedicalRecordResponse(

	@field:SerializedName("message")
	val msg: String? = null,

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DataItem(

	@field:SerializedName("beratPet")
	val beratPet: Int,

	@field:SerializedName("kesehatan")
	val kesehatan: String,

	@field:SerializedName("xRay")
	val xRay: String? = null,

	@field:SerializedName("catatan")
	val catatan: String,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("alamat")
	val alamat: String,

	@field:SerializedName("descKesehatan")
	val descKesehatan: String,

	@field:SerializedName("namePet")
	val namePet: String,

	@field:SerializedName("update_at")
	val updateAt: String? = null,

	@field:SerializedName("medicalId")
	val medicalId: Int? = null,

	@field:SerializedName("tanggal")
	val tanggal: String,

	@field:SerializedName("klinikName")
	val klinikName: String,

	@field:SerializedName("dokterName")
	val dokterName: String,

	@field:SerializedName("kategoriPet")
	val kategoriPet: String,

	@field:SerializedName("info")
	val info: String
)
