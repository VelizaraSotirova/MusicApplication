package com.example.music_backend.service;

import com.example.music_backend.dto.LoginRequestDto;
import com.example.music_backend.dto.RegisterRequestDto;
import com.example.music_backend.dto.UserDto;
import com.example.music_backend.model.User;
import com.example.music_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDto registerUser(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username unavailable!");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        return new UserDto(savedUser.getId(), savedUser.getUsername());
    }

    public Optional<UserDto> login(LoginRequestDto request) {
        return userRepository.findByUsername(request.username())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(user -> new UserDto(user.getId(), user.getUsername()));
    }
}