package com.example.musicappmobile.data.model

import com.google.gson.annotations.SerializedName

data class MergeResponse(
    @SerializedName("songsAdded") val songsAdded: Int,
    @SerializedName("duplicatesSkipped") val duplicatesSkipped: Int,
    @SerializedName("message") val message: String
)