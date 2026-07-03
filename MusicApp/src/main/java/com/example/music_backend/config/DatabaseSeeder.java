package com.example.music_backend.config;

import com.example.music_backend.dto.AddSongRequestDto;
import com.example.music_backend.model.User;
import com.example.music_backend.repository.UserRepository;
import com.example.music_backend.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CatalogService catalogService;

    @Override
    public void run(String... args) throws Exception {
        // Check if already has data
        if (userRepository.count() == 0) {
            System.out.println("Starting Database Seeding...");


            User user1 = new User();
            user1.setUsername("velizara");
            user1.setPassword("1234");
            userRepository.save(user1);

            User user2 = new User();
            user2.setUsername("ivan");
            user2.setPassword("password");
            userRepository.save(user2);

            System.out.println("Created test users: 'velizara' and 'ivan'");


            String sharedPlaylistId = "party-rock-2026";


            catalogService.addSong(user1, new AddSongRequestDto(
                    sharedPlaylistId, "Blinding Lights", "The Weeknd", 5
            ));

            catalogService.addSong(user1, new AddSongRequestDto(
                    sharedPlaylistId, "Shape of You", "Ed Sheeran", 4
            ));

            catalogService.addSong(user2, new AddSongRequestDto(
                    sharedPlaylistId, "Bohemian Rhapsody", "Queen", 5
            ));

            catalogService.addSong(user2, new AddSongRequestDto(
                    sharedPlaylistId, "Bad Guy", "Billie Eilish", 3
            ));

            System.out.println("Successfully seeded 4 initial songs into playlist: " + sharedPlaylistId);
            System.out.println("Local JSON journal sync completed!");
            System.out.println("Database Seeding finished successfully!");
        } else {
            System.out.println("Database already contains data. Skipping seeder.");
        }
    }
}