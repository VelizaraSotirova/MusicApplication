package com.example.music_backend.dto;

public record AddSongRequestDto(
        String playlistId,
        String title,
        String artist,
        int rating
) {
}
