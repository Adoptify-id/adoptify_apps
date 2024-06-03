package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("data")
    val data: List<DataUser?>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class DataUser(
    @field:SerializedName("provinsi")
    val provinsi: String? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("roleId")
    val roleId: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("userId")
    val userId: Int? = null,

    @field:SerializedName("noTelp")
    val noTelp: String? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null,

    @field:SerializedName("points")
    val points: Int? = null,

    @field:SerializedName("tglLahir")
    val tglLahir: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("update_at")
    val updateAt: String? = null,

    @field:SerializedName("kodePos")
    val kodePos: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("fullName")
    val fullName: String? = null,

    @field:SerializedName("foto")
    val foto: String? = null,
)
