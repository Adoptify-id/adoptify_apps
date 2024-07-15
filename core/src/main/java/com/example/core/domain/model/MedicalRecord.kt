package com.example.core.domain.model

data class MedicalRecord(
	val msg: String? = null,
	val data: List<MedicalItem?>? = null,
	val status: Int? = null
)

data class MedicalItem(
	val beratPet: Int? = null,
	val kesehatan: String? = null,
	val xRay: String? = null,
	val catatan: String? = null,
	val createdAt: String? = null,
	val userId: Int? = null,
	val alamat: String? = null,
	val descKesehatan: String? = null,
	val namePet: String? = null,
	val updateAt: String? = null,
	val medicalId: Int? = null,
	val tanggal: String? = null,
	val klinikName: String? = null,
	val dokterName: String? = null,
	val kategoriPet: String? = null,
	val info: String? = null
)

