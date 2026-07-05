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


    @POST("api/catalog/undo")
    suspend fun undoAction(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Multipart
    @POST("api/catalog/merge")
    suspend fun mergeCatalog(
        @Header("Authorization") token: String,
        @Part("playlistId") playlistId: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<MergeResponse>
}