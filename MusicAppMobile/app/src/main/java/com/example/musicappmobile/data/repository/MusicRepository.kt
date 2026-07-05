package com.example.musicappmobile.data.repository

import com.example.musicappmobile.data.model.AddSongRequest
import com.example.musicappmobile.data.model.MergeResponse
import com.example.musicappmobile.data.model.SongResponse
import com.example.musicappmobile.data.model.UndoRequest
import com.example.musicappmobile.data.network.RetrofitClient
import com.example.musicappmobile.utils.TokenManager
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class MusicRepository(private val tokenManager: TokenManager) {

    private val apiService = RetrofitClient.musicApiService


    private fun getAuthHeader(): String {
        val token = tokenManager.getToken() ?: ""
        return "Bearer $token"
    }


    suspend fun addSong(token: String, request: AddSongRequest): Response<SongResponse> {
        return apiService.addSong(token, request)
    }


    suspend fun undoAction(token: String): Response<ResponseBody> {
        return apiService.undoAction(token)
    }


    suspend fun mergeCatalog(playlistId: RequestBody, file: MultipartBody.Part): Response<MergeResponse> {
        return apiService.mergeCatalog(getAuthHeader(), playlistId, file)
    }
}