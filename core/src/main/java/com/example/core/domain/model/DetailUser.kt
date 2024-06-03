package com.example.core.domain.model

import com.google.gson.annotations.SerializedName

data class DetailUser(
    val msg: String? = null,
    val data: List<DetailDataUser?>? = null,
    val status: Int? = null
)

data class DetailDataUser(
    val provinsi: String? = null,
    val gender: String? = null,
    val roleId: Int? = null,
    val createdAt: String? = null,
    val userId: Int? = null,
    val noTelp: String? = null,
    val alamat: String? = null,
    val points: Int? = null,
    val tglLahir: String? = null,
    val password: String? = null,
    val updateAt: String? = null,
    val kodePos: String? = null,
    val email: String? = null,
    val username: String? = null,
    val fullName: String? = null,
    val foto: String? = null,
)
