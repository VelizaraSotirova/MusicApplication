package com.example.musicappmobile.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicappmobile.data.model.AddSongRequest
import com.example.musicappmobile.data.model.SongResponse
import com.example.musicappmobile.data.repository.MusicRepository
import kotlinx.coroutines.launch

class MusicViewModel(private val repository: MusicRepository) : ViewModel() {

    private val _songs = MutableLiveData<List<SongResponse>>(emptyList())
    val songs: LiveData<List<SongResponse>> = _songs

    private val _uiMessage = MutableLiveData<String?>()
    val uiMessage: LiveData<String?> = _uiMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var userToken: String? = null

    fun setAuthToken(token: String) {
        val cleanToken = token.replace("\"", "")
        userToken = "Bearer $cleanToken"
    }

    fun logout() {
        userToken = null
        _songs.value = emptyList()
        _uiMessage.value = null
    }

    fun addSong(playlistId: String, title: String, artist: String, rating: Int) {
        val token = userToken
        if (token == null) {
            _uiMessage.value = "Error: not authorized!"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.addSong(token, AddSongRequest(playlistId, title, artist, rating))
                if (response.isSuccessful && response.body() != null) {
                    val newSong = response.body()!!

                    val currentList = _songs.value.orEmpty().toMutableList()
                    currentList.add(newSong)
                    _songs.value = currentList

                    _uiMessage.value = "Song '${newSong.title}' is added successfully!"
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

    fun undoLastAction() {
        val token = userToken
        if (token == null) {
            _uiMessage.value = "Error: Missing token!"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.undoAction(token)
                if (response.isSuccessful) {
                    val message = response.body()?.string() ?: "Action undone!"
                    _uiMessage.value = message

                    val currentList = _songs.value.orEmpty().toMutableList()
                    if (currentList.isNotEmpty()) {
                        currentList.removeAt(currentList.size - 1)
                        _songs.value = currentList
                    }
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

    fun clearMessage() {
        _uiMessage.value = null
    }
}