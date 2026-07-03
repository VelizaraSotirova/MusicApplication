package com.example.music_backend.controller;

import com.example.music_backend.dto.AddSongRequestDto;
import com.example.music_backend.dto.MergeResponseDto;
import com.example.music_backend.dto.SongDto;
import com.example.music_backend.dto.UndoRequestDto;
import com.example.music_backend.model.User;
import com.example.music_backend.repository.UserRepository;
import com.example.music_backend.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

/*
 * TODO: FUTURE ENHANCEMENTS FOR MUSIC MANAGEMENT (KOTLIN FRONTEND COMPATIBILITY):
 * * 1. SPRING SECURITY CONTEXT INTEGRATION:
 * - Remove the temporary `@RequestHeader("X-Username")`.
 * - Extract the authenticated user automatically from the secure Security Context after JWT validation.
 * * 2. OPTIMIZED STREAMING FOR FILE MERGING:
 * - Replace `File.createTempFile()` with memory-efficient stream processing directly
 * via `file.getInputStream()` to avoid clogging the server's hard drive during catalog merging.
 * - Validate the uploaded file's MIME type to explicitly accept only "application/json".
 */

@RestController
@RequestMapping("/api/catalog")
@CrossOrigin(origins = "*")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private UserRepository userRepository; // Temporary use for searching test user by username


    // Path: POST http://localhost:8080/api/catalog/add
    @PostMapping("/add")
    public ResponseEntity<?> addSong(@RequestHeader("X-Username") String username, @RequestBody AddSongRequestDto request) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).body("User not found!");

        SongDto response = catalogService.addSong(user, request);
        return ResponseEntity.ok(response);
    }


    // Path: POST http://localhost:8080/api/catalog/undo
    @PostMapping("/undo")
    public ResponseEntity<?> undoAction(@RequestHeader("X-Username") String username, @RequestBody UndoRequestDto request) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).body("User not found!");

        catalogService.undoLastAction(user, request.playlistId());
        return ResponseEntity.ok("Last Action successfully undone (Undo)!");
    }


    // Path: POST http://localhost:8080/api/catalog/merge?playlistId=...
    @PostMapping("/merge")
    public ResponseEntity<?> mergeCatalog(
            @RequestHeader("X-Username") String username,
            @RequestParam("playlistId") String playlistId,
            @RequestParam("file") MultipartFile file) {

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).body("User not found!");

        if (file.isEmpty()) return ResponseEntity.badRequest().body("Please upload a valid JSON file!");

        try {
            // Temporary write uploaded from a phone file, so BufferedReader can read it
            File tempFile = File.createTempFile("friend_catalog_", ".json");
            file.transferTo(tempFile);

            MergeResponseDto response = catalogService.mergeFromFriendFile(user, playlistId, tempFile);

            // Delete temporary file after operation is done
            tempFile.delete();

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }
}