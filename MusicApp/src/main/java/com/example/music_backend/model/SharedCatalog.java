package com.example.music_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "shared_catalogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharedCatalog {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name = "playlist_id", nullable = false)
    private String playlistId; // Indicator for shared playlist / catalog

    @Column(name = "song_id", nullable = false)
    private String songId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private int rating;

    @Column(name = "added_by_username", nullable = false)
    private String addedByUsername;
}
