package com.example.music_backend.controller;

import com.example.music_backend.dto.LoginRequestDto;
import com.example.music_backend.dto.RegisterRequestDto;
import com.example.music_backend.dto.UserDto;
import com.example.music_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // allows mobile phone to connect flawlessly with backend
public class UserController {

    @Autowired
    private UserService userService;

    // Path: POST http://localhost:8080/api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        try {
            UserDto registeredUser = userService.registerUser(request);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            // unavailable username -> return error 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Path: POST http://localhost:8080/api/users/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        var userOpt = userService.login(request);

        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(401).body("Invalid username or password!");
        }
    }
}