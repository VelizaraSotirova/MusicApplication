package com.example.musicappmobile.ui.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onAuthSuccess: (String) -> Unit
) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }

    val isLoading by authViewModel.isLoading.observeAsState(initial = false)
    val loginResult by authViewModel.loginResult.observeAsState()
    val registerResult by authViewModel.registerResult.observeAsState()


    LaunchedEffect(loginResult) {
        loginResult?.onSuccess { authResponse ->
            Toast.makeText(context, "Welcome, ${authResponse.username}!", Toast.LENGTH_SHORT).show()
            onAuthSuccess(authResponse.token)
            authViewModel.clearResult()
        }
        loginResult?.onFailure { error ->
            Toast.makeText(context, error.message ?: "Login error", Toast.LENGTH_LONG).show()
            authViewModel.clearResult()
        }
    }


    LaunchedEffect(registerResult) {
        registerResult?.onSuccess {
            Toast.makeText(context, "Successful registration! You can login now.", Toast.LENGTH_LONG).show()
            isLoginMode = true
            authViewModel.clearResult()
        }
        registerResult?.onFailure { error ->
            Toast.makeText(context, error.message ?: "Registration error", Toast.LENGTH_LONG).show()
            authViewModel.clearResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLoginMode) "Log in MusicApp" else "Registration",
            fontSize = 28.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(visible = isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please, fill all fields!", Toast.LENGTH_SHORT).show()
                } else {
                    if (isLoginMode) {
                        authViewModel.login(username, password)
                    } else {
                        authViewModel.register(username, password)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading
        ) {
            Text(text = if (isLoginMode) "LOGIN" else "REGISTER")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { isLoginMode = !isLoginMode },
            enabled = !isLoading
        ) {
            Text(
                text = if (isLoginMode) "Haven't got profile yet? Register here"
                else "Already have an account? Log in here"
            )
        }
    }
}