package com.example.musicappmobile.data.network

import com.example.musicappmobile.data.model.AuthResponse
import com.example.musicappmobile.data.model.LoginRequest
import com.example.musicappmobile.data.model.RegisterRequest
import com.example.musicappmobile.data.model.UserResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}

object RetrofitClient {
    private const val BASE_URL = "http://:8080/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApiService: UserApiService by lazy {
        Retrofit.Builder()
            retrofit.create(UserApiService::class.java)
    }

    val musicApiService: MusicApiService by lazy {
        retrofit.create(MusicApiService::class.java)
    }
}