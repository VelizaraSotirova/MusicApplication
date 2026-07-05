package com.example.musicappmobile.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse (
    @SerializedName("username") val username: String,
    @SerializedName("token") val token: String
) {}