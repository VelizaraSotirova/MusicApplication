package com.example.music_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "history_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryLog {

    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "command_type", nullable = false)
    private String commandType; // ADD, REMOVE, MERGE

    // Command status - if the command is active ("executed") or canceled with "undo" ("undone")
    @Column(nullable = false)
    private String status = "executed";

    @Column(name = "song_id", nullable = false)
    private String songId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    @jakarta.validation.constraints.Min(1)
    @jakarta.validation.constraints.Max(5)
    private int rating;

    @Column(name = "playlist_id")
    private String playlistId;
}
