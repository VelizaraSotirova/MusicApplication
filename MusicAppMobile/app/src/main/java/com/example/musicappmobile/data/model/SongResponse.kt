package com.example.musicappmobile.data.model

import com.google.gson.annotations.SerializedName

data class SongResponse(
    @SerializedName("songId") val songId: String,
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("rating") val rating: Int
)