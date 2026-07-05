package com.example.music_backend.controller;

import com.example.music_backend.dto.AuthResponseDto;
import com.example.music_backend.dto.LoginRequestDto;
import com.example.music_backend.dto.RegisterRequestDto;
import com.example.music_backend.dto.UserDto;
import com.example.music_backend.security.JwtUtil;
import com.example.music_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // allows mobile phone to connect flawlessly with backend
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Path: POST http://localhost:8080/api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request) {
        UserDto registeredUser = userService.registerUser(request);
        return ResponseEntity.ok(registeredUser);
    }

    // Path: POST http://localhost:8080/api/users/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        var userOpt = userService.login(request);

        if (userOpt.isPresent()) {
            UserDto userDto = userOpt.get();
            String token = jwtUtil.generateToken(userDto.username());
            return ResponseEntity.ok(new AuthResponseDto(userDto.username(), token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!");
        }
    }
}