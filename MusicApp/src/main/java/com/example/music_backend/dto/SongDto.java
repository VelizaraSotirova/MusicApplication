package com.example.music_backend.dto;

public record SongDto(
        String songId,
        String title,
        String artist,
        int rating
) {
}
