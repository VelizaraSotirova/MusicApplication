package com.example.musicappmobile.data.network

import com.example.musicappmobile.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MusicApiService {

    @POST("api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<UserResponse>

    @POST("api/users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/catalog/add")
    suspend fun addSong(
        @Header("Authorization") token: String,
        @Body request: AddSongRequest
    ): Response<SongResponse>

    @GET("api/catalog/songs/my-songs")
    suspend fun getUserSongs(
        @Header("Authorization") token: String
    ): Response<List<SharedCatalogResponse>>

    @POST("api/catalog/undo")
    suspend fun undoAction(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @DELETE("api/catalog/delete/{songId}")
    suspend fun deleteSong(
        @Header("Authorization") token: String,
        @Path("songId") songId: String
    ): Response<Void>


    @Multipart
    @POST("api/catalog/merge/{playlistId}")
    suspend fun mergeCatalog(
        @Header("Authorization") token: String,
        @Path("playlistId") playlistId: String,
        @Part file: MultipartBody.Part
    ): Response<MergeResponse>
}