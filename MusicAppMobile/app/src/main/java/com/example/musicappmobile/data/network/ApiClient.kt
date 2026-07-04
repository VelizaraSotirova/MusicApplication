package com.example.musicappmobile.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Gson to translate JSON <-> Kotlin
            .build()
    }


    val apiService: MusicApiService by lazy {
        retrofit.create(MusicApiService::class.java)
    }
}