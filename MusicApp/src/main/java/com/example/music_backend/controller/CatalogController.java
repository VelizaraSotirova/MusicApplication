package com.example.music_backend.controller;

import com.example.music_backend.dto.AddSongRequestDto;
import com.example.music_backend.dto.MergeResponseDto;
import com.example.music_backend.dto.SongDto;
import com.example.music_backend.model.SharedCatalog;
import com.example.music_backend.model.User;
import com.example.music_backend.repository.UserRepository;
import com.example.music_backend.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/catalog")
@CrossOrigin(origins = "*")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/add")
    public ResponseEntity<?> addSong(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddSongRequestDto request) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        SongDto response = catalogService.addSong(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/songs/my-songs")
    public ResponseEntity<List<SharedCatalog>> getMySongs(java.security.Principal principal) {
        String username = principal.getName();

        List<SharedCatalog> songs = catalogService.getSongsByUsername(username);
        return ResponseEntity.ok(songs);
    }

    @PostMapping("/undo")
    public ResponseEntity<?> undoAction(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        catalogService.undoLastAction(user);
        return ResponseEntity.ok("Last Action successfully undone (Undo)!");
    }

    @DeleteMapping("/delete/{songId}")
    public ResponseEntity<?> deleteSong(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String songId) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        catalogService.removeSong(user, songId);
        return ResponseEntity.ok("Song removed successfully");
    }

    @PostMapping("/merge/{playlistId}")
    public ResponseEntity<MergeResponseDto> mergeFromFriend(
            @PathVariable String playlistId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        try {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            MergeResponseDto response = catalogService.mergeFromFriendStream(user, playlistId, file.getInputStream());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/export-file")
    public ResponseEntity<Resource> exportCatalogFile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        String filePath = System.getProperty("user.home") + "/music_catalogs/" + username + "_songs.json";

        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(resource);
    }
}