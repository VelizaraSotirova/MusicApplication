package com.example.musicappmobile.data.model

data class SharedCatalogResponse(
    val id: String,
    val playlistId: String,
    val songId: String,
    val title: String,
    val artist: String,
    val rating: Int,
    val addedByUsername: String
)