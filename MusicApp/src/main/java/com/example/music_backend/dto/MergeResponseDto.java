package com.example.music_backend.dto;

public record MergeResponseDto(
        int songsAdded,
        int duplicatesSkipped,
        String message
) {
}
