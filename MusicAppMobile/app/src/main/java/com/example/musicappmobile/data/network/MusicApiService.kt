package com.example.musicappmobile.data.network

import com.example.musicappmobile.data.model.AddSongRequest
import com.example.musicappmobile.data.model.LoginRequest
import com.example.musicappmobile.data.model.MergeResponse
import com.example.musicappmobile.data.model.RegisterRequest
import com.example.musicappmobile.data.model.SongResponse
import com.example.musicappmobile.data.model.UndoRequest
import com.example.musicappmobile.data.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MusicApiService {

    @POST("api/users/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<UserResponse>

    @POST("api/users/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<UserResponse>

    @POST("api/catalog/add")
    suspend fun addSong(
        @Header("X-Username") username: String,
        @Body request: AddSongRequest
    ): Response<SongResponse>

    @POST("api/catalog/undo")
    suspend fun undoAction(
        @Header("X-Username") username: String,
        @Body request: UndoRequest
    ): Response<String> // returns text message from backend

    @Multipart
    @POST("api/catalog/merge")
    suspend fun mergeCatalog(
        @Header("X-Username") username: String,
        @Part("playlistId") playlistId: RequestBody, // Send as text component
        @Part file: MultipartBody.Part               // Send as real file (bytes)
    ): Response<MergeResponse>
}