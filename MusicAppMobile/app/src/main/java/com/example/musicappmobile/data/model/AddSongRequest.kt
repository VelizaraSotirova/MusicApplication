package com.example.musicappmobile.data.model

import com.google.gson.annotations.SerializedName

data class AddSongRequest(
    @SerializedName("playlistId") val playlistId: String,
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("rating") val rating: Int
)