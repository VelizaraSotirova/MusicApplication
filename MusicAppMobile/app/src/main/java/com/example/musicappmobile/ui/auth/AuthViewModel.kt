package com.example.musicappmobile.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicappmobile.data.model.AuthResponse
import com.example.musicappmobile.data.model.LoginRequest
import com.example.musicappmobile.data.model.RegisterRequest
import com.example.musicappmobile.data.model.UserResponse
import com.example.musicappmobile.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<AuthResponse>?>()
    val loginResult: LiveData<Result<AuthResponse>?> = _loginResult

    private val _registerResult = MutableLiveData<Result<UserResponse>?>()
    val registerResult: LiveData<Result<UserResponse>?> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun logout() {
        _loginResult.value = null
        _registerResult.value = null
        _isLoading.value = false
    }

    fun login(username: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.loginUser(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    _loginResult.value = Result.success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Invalid username or password!"
                    _loginResult.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(Exception("No connection with server: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(username: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.registerUser(RegisterRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    _registerResult.value = Result.success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Username already used!"
                    _registerResult.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(Exception("No connection with server: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun clearResult() {
        _loginResult.value = null
        _registerResult.value = null
    }
}

