package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("access_token")
    val accessToken: String,

    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("userId")
    val userId: Int,

    @field:SerializedName("roleId")
    val roleId: Int,
)
