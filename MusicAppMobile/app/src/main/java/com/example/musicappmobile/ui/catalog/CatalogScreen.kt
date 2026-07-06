package com.example.musicappmobile.ui.catalog

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicappmobile.data.model.SongResponse
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(viewModel: MusicViewModel, onLogout: () -> Unit) {
    val context = LocalContext.current
    val playlists = remember { listOf("Favourites", "Car", "Relax", "Sport") }

    var expanded by remember { mutableStateOf(false) }
    var selectedPlaylist by remember { mutableStateOf(playlists[0]) }
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var canUndo by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val localSongs by viewModel.songs.observeAsState(initial = emptyList())
    val databaseSongs by viewModel.songsList.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val uiMessage by viewModel.uiMessage.observeAsState()

    val sortOptions = listOf("Title", "Artist", "Rating")
    var selectedSortOption by remember { mutableStateOf(sortOptions[0]) }

    // File Picker Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val file = File(context.cacheDir, "friend_catalog.json")
            inputStream?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            viewModel.mergeCatalog(selectedPlaylist, file)
        }
    }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            if (it.contains("successfully", ignoreCase = true)) {
                canUndo = !it.contains("undo", ignoreCase = true)
            }
            viewModel.clearMessage()
        }
    }

    val allSongsCombined = remember(databaseSongs, localSongs, selectedSortOption, searchQuery) {
        val databaseAsSongs = databaseSongs.map { SongResponse(it.songId, it.title, it.artist, it.rating) }
        val combined = databaseAsSongs + localSongs

        val filtered = if (searchQuery.isBlank()) combined else {
            combined.filter { it.title.contains(searchQuery, ignoreCase = true) || it.artist.contains(searchQuery, ignoreCase = true) }
        }

        when (selectedSortOption) {
            "Title" -> filtered.sortedBy { it.title.lowercase() }
            "Artist" -> filtered.sortedBy { it.artist.lowercase() }
            "Rating" -> filtered.sortedByDescending { it.rating }
            else -> filtered
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Music catalog", fontSize = 24.sp, style = MaterialTheme.typography.headlineMedium)
            Row {
                Button(onClick = { launcher.launch("application/json") }, modifier = Modifier.padding(end = 8.dp)) { Text("Merge") }
                Button(onClick = { canUndo = false; onLogout() }) { Text("Logout") }
            }
        }

        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            label = { Text("Search by title or artist...") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Add new song", style = MaterialTheme.typography.titleMedium)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedPlaylist, onValueChange = {}, readOnly = true,
                        label = { Text("Choose playlist") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        playlists.forEach { name -> DropdownMenuItem(text = { Text(name) }, onClick = { selectedPlaylist = name; expanded = false }) }
                    }
                }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Song title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = artist, onValueChange = { artist = it }, label = { Text("Artist") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = rating, onValueChange = { rating = it }, label = { Text("Rating (1-5)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val ratingInt = rating.toIntOrNull()
                        if (title.isBlank() || artist.isBlank() || ratingInt == null) {
                            Toast.makeText(context, "Fill all fields correctly!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addSong(selectedPlaylist, title, artist, ratingInt)
                            title = ""; artist = ""; rating = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp), enabled = !isLoading
                ) { Text("ADD SONG") }
            }
        }

        AnimatedVisibility(visible = canUndo) {
            Button(
                onClick = { viewModel.undoLastAction(); canUndo = false },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) { Text("UNDO LAST ACTIVITY") }
        }

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            items(items = allSongsCombined, key = { it.songId }) { song ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = song.title, style = MaterialTheme.typography.bodyLarge)
                            Text(text = "from ${song.artist}", style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { viewModel.deleteSong(song.songId) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                        Text(text = "⭐ ${song.rating}", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}