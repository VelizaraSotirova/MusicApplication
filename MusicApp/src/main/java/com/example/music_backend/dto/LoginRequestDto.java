package com.example.music_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Username is required!")
        String username,

        @NotBlank(message = "Password is required!")
        String password
) {
}
