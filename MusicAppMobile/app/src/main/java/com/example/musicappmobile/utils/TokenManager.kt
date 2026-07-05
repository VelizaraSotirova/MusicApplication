package com.example.musicappmobile.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("AUTH_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "JWT_TOKEN"
        private const val USERNAME_KEY = "LOGGED_IN_USER"
    }

    fun saveAuthToken(token: String, username: String) {
        prefs.edit {
            putString(TOKEN_KEY, token)
            putString(USERNAME_KEY, username)
        }
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun getUsername(): String? {
        return prefs.getString(USERNAME_KEY, null)
    }

    fun clearAuth() {
        prefs.edit(commit = true) {
            remove(TOKEN_KEY)
            remove(USERNAME_KEY)
        }
    }
}