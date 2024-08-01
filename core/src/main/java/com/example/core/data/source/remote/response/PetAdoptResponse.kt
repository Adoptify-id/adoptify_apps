package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class PetAdoptResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("data")
	val data: List<PetAdoptItem>,

	@field:SerializedName("status")
	val status: Int? = null
)

data class PetAdoptItem(
	@field:SerializedName("fotoPet")
	val fotoPet: String? = null,

	@field:SerializedName("petId")
	val petId: Int? = null,

	@field:SerializedName("umur")
	val umur: Int? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("ras")
	val ras: String? = null,

	@field:SerializedName("descPet")
	val descPet: String? = null,

	@field:SerializedName("namePet")
	val namePet: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("isAdopt")
	val isAdopt: Boolean? = false,

	@field:SerializedName("update_at")
	val updateAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("provinsi")
	val provinsi: String? = null,

	@field:SerializedName("foto")
	val foto: String? = null,
)
