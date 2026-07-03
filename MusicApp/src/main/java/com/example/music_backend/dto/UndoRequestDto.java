package com.example.music_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UndoRequestDto(
        @NotBlank(message = "Playlist ID is required for Undo operation!")
        String playlistId
) {
}
