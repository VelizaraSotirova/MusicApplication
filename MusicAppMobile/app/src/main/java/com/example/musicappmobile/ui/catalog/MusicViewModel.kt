package com.example.musicappmobile.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicappmobile.data.model.AddSongRequest
import com.example.musicappmobile.data.model.SharedCatalogResponse
import com.example.musicappmobile.data.model.SongResponse
import com.example.musicappmobile.data.repository.MusicRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MusicViewModel(private val repository: MusicRepository) : ViewModel() {

    private val _songs = MutableLiveData<List<SongResponse>>(emptyList())
    val songs: LiveData<List<SongResponse>> = _songs

    private val _uiMessage = MutableLiveData<String?>()
    val uiMessage: LiveData<String?> = _uiMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _songsList = MutableLiveData<List<SharedCatalogResponse>>(emptyList())
    val songsList: LiveData<List<SharedCatalogResponse>> = _songsList

    private var userToken: String? = null

    fun setAuthToken(token: String) {
        val cleanToken = token.replace("\"", "").replace("Bearer ", "")
        userToken = "Bearer $cleanToken"
    }

    fun logout() {
        userToken = null
        _songs.value = emptyList()
        _songsList.value = emptyList()
        _uiMessage.value = null
    }

    fun addSong(playlistId: String, title: String, artist: String, rating: Int) {
        val token = userToken ?: run {
            _uiMessage.value = "Error: not authorized!"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response =
                    repository.addSong(token, AddSongRequest(playlistId, title, artist, rating))
                if (response.isSuccessful) {
                    _uiMessage.value = "Song '$title' added successfully!"
                    loadUserSongs()
                } else {
                    _uiMessage.value = "Error adding song: code ${response.code()}"
                }
            } catch (e: Exception) {
                _uiMessage.value = "No server connection: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserSongs() {
        val token = userToken ?: return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getUserSongs(token)
                if (response.isSuccessful && response.body() != null) {
                    val uniqueSongs = response.body()!!.distinctBy { it.id }
                    _songsList.value = uniqueSongs
                } else {
                    _uiMessage.value = "Error loading songs: code ${response.code()}"
                }
            } catch (e: Exception) {
                _uiMessage.value = "No server connection: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSong(songId: String) {
        val token = userToken ?: return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteSong(token, songId)
                if (response.isSuccessful) {
                    _uiMessage.value = "Deleted successfully!"
                    loadUserSongs()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _uiMessage.value = "Failed: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                _uiMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun undoLastAction() {
        val token = userToken ?: return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.undoAction(token)
                if (response.isSuccessful) {
                    _uiMessage.value = "Action undone successfully!"
                    loadUserSongs()
                } else {
                    _uiMessage.value = "Undo error: code ${response.code()}"
                }
            } catch (e: Exception) {
                _uiMessage.value = "Communication exception: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun mergeCatalog(playlistId: String, file: File) {
        val fileBody = file.asRequestBody("application/json".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, fileBody)

        viewModelScope.launch {
            try {
                val response = repository.mergeCatalog(playlistId, filePart)

                if (response.isSuccessful) {
                    val data = response.body()
                    _uiMessage.value = "Successfully added: ${data?.songsAdded}, ${data?.duplicatesSkipped} skipped."
                    loadUserSongs()
                } else {
                    _uiMessage.value = "Server error: ${response.code()}"
                }
            } catch (e: Exception) {
                _uiMessage.value = "Connection error: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _uiMessage.value = null
    }
}