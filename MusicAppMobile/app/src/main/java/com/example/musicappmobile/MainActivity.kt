package com.example.musicappmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.example.musicappmobile.data.repository.MusicRepository
import com.example.musicappmobile.data.repository.UserRepository
import com.example.musicappmobile.ui.auth.AuthScreen
import com.example.musicappmobile.ui.auth.AuthViewModel
import com.example.musicappmobile.ui.auth.AuthViewModelFactory
import com.example.musicappmobile.ui.catalog.CatalogScreen
import com.example.musicappmobile.ui.catalog.MusicViewModel
import com.example.musicappmobile.ui.catalog.MusicViewModelFactory
import com.example.musicappmobile.utils.TokenManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        val userRepository = UserRepository(tokenManager)
        val musicRepository = MusicRepository(tokenManager)

        val authFactory = AuthViewModelFactory(userRepository)
        val authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

        val musicFactory = MusicViewModelFactory(musicRepository)
        val musicViewModel = ViewModelProvider(this, musicFactory)[MusicViewModel::class.java]

        setContent {
            var isLoggedIn by remember { mutableStateOf(tokenManager.getToken() != null) }

            LaunchedEffect(isLoggedIn) {
                val currentToken = tokenManager.getToken()
                if (isLoggedIn && currentToken != null) {
                    musicViewModel.setAuthToken(currentToken)
                    musicViewModel.loadUserSongs()
                }
            }

            if (!isLoggedIn) {
                AuthScreen(
                    authViewModel = authViewModel,
                    onAuthSuccess = { token ->
                        tokenManager.saveAuthToken(token, "logged_user")
                        musicViewModel.setAuthToken(token)
                        isLoggedIn = true
                    }
                )
            } else {
                CatalogScreen(
                    viewModel = musicViewModel,
                    onLogout = {
                        isLoggedIn = false

                        tokenManager.clearAuth()
                        musicViewModel.logout()
                        authViewModel.logout()
                    }
                )
            }
        }
    }
}