package com.example.musicappmobile.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login screen
        composable(route = "login") {
            LoginScreen(
                onLoginClick = { user, pass ->
                    // Later will invoke ApiClient.apiService.loginUser
                    // Successful login:
                    navController.navigate("catalog")
                },
                onNavigateToRegister = {
                    navController.navigate("register") // Navigate to register
                }
            )
        }

        // Register screen
        composable(route = "register") {
            RegisterScreen(
                onRegisterClick = { user, pass ->
                    // Will invoke register
                },
                onNavigateToLogin = {
                    navController.popBackStack() // Go one screen back(login)
                }
            )
        }

        // Music catalog (temporary empty)
        composable(route = "catalog") {
            // Here will be the screen with songs, undo and merge operations
            Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                androidx.compose.material3.Text("Your music catalog (Soon available!)", fontSize = 24.sp)
            }
        }
    }
}