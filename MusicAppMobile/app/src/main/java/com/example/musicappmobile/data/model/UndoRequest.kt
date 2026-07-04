package com.example.musicappmobile.data.model

import com.google.gson.annotations.SerializedName

data class UndoRequest(
    @SerializedName("playlistId") val playlistId: String
)