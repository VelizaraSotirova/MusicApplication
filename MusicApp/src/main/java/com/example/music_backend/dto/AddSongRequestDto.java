package com.example.music_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddSongRequestDto(
        @NotBlank(message = "Playlist ID is required!")
        String playlistId,

        @NotBlank(message = "Song title cannot be blank!")
        String title,

        @NotBlank(message = "Artist name cannot be blank!")
        String artist,

        @Min(value = 1, message = "Rating must be at least 1!")
        @Max(value = 5, message = "Rating cannot be higher than 5!")
        int rating
) {
}
