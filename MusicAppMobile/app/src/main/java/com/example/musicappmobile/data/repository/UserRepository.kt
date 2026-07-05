package com.example.musicappmobile.data.repository

import com.example.musicappmobile.data.model.AuthResponse
import com.example.musicappmobile.data.model.LoginRequest
import com.example.musicappmobile.data.model.RegisterRequest
import com.example.musicappmobile.data.model.UserResponse
import com.example.musicappmobile.data.network.RetrofitClient
import com.example.musicappmobile.utils.TokenManager
import retrofit2.Response

class UserRepository(private val tokenManager: TokenManager) {

    private val apiService = RetrofitClient.userApiService


    suspend fun registerUser(request: RegisterRequest): Response<UserResponse> {
        return apiService.register(request)
    }


    suspend fun loginUser(request: LoginRequest): Response<AuthResponse> {
        val response = apiService.login(request)

        if (response.isSuccessful && response.body() != null) {
            val authBody = response.body()!!
            tokenManager.saveAuthToken(authBody.token, authBody.username)
        }

        return response
    }


    fun logout() {
        tokenManager.clearAuth()
    }
}