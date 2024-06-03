package com.example.core.domain.model

import com.google.gson.annotations.SerializedName

data class Login(
    val accessToken: String? = null,
    val username: String,
    val userId: Int,
    val roleId: Int,
)
