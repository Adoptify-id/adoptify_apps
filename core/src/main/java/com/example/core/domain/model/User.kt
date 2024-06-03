package com.example.core.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("noTelp")
    val phone: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("roleId")
    val roleId: Int? = 1,
)
