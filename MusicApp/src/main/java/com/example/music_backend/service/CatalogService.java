package com.example.music_backend.service;

import com.example.music_backend.dto.AddSongRequestDto;
import com.example.music_backend.dto.MergeResponseDto;
import com.example.music_backend.dto.SongDto;
import com.example.music_backend.model.HistoryLog;
import com.example.music_backend.model.SharedCatalog;
import com.example.music_backend.model.User;
import com.example.music_backend.repository.HistoryLogRepository;
import com.example.music_backend.repository.SharedCatalogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    @Autowired
    private HistoryLogRepository historyRepository;

    @Autowired
    private SharedCatalogRepository sharedRepository;

    @Autowired
    private JsonJournalService journalService;


    public SongDto addSong(User user, AddSongRequestDto request) {
        String songId = UUID.randomUUID().toString();

        SharedCatalog shared = new SharedCatalog();
        shared.setPlaylistId(request.playlistId());
        shared.setSongId(songId);
        shared.setTitle(request.title());
        shared.setArtist(request.artist());
        shared.setRating(request.rating());
        shared.setAddedByUsername(user.getUsername());
        SharedCatalog saved = sharedRepository.save(shared);

        HistoryLog log = new HistoryLog();
        log.setUser(user);
        log.setCommandType("ADD");
        log.setPlaylistId(request.playlistId());
        log.setSongId(songId);
        log.setTitle(request.title());
        log.setArtist(request.artist());
        log.setRating(request.rating());
        log.setStatus("executed");
        log.setSharedCatalogId(saved.getId());
        historyRepository.save(log);

        // trigger a file update through its service
        triggerJsonSync(user);

        return new SongDto(songId, request.title(), request.artist(), request.rating());
    }

    public List<SharedCatalog> getSongsByUsername(String username) {
        return sharedRepository.findByAddedByUsername(username);
    }


    @Transactional
    public void undoLastAction(User user) {
        List<HistoryLog> userHistory = historyRepository.findByUser(user);

        HistoryLog lastExecuted = userHistory.stream()
                .filter(log -> log.getStatus().equals("executed"))
                .reduce((first, second) -> second)
                .orElse(null);

        if (lastExecuted != null) {
            lastExecuted.setStatus("undone");
            historyRepository.save(lastExecuted);

            if (lastExecuted.getCommandType().equals("ADD")) {
                if (lastExecuted.getSharedCatalogId() != null) {
                    sharedRepository.deleteById(lastExecuted.getSharedCatalogId());
                }
            }
            else if (lastExecuted.getCommandType().equals("REMOVE")) {
                SharedCatalog restoredSong = new SharedCatalog();
                restoredSong.setSongId(lastExecuted.getSongId());
                restoredSong.setPlaylistId(lastExecuted.getPlaylistId());
                restoredSong.setTitle(lastExecuted.getTitle());
                restoredSong.setArtist(lastExecuted.getArtist());
                restoredSong.setRating(lastExecuted.getRating());
                restoredSong.setAddedByUsername(user.getUsername());

                sharedRepository.save(restoredSong);
            }

            triggerJsonSync(user);
        }
    }

    @Transactional
    public void removeSong(User user, String songId) {
        SharedCatalog songToDelete = sharedRepository.findBySongIdAndAddedByUsername(songId, user.getUsername())
                .orElseThrow(() -> new RuntimeException("Song not found"));

        HistoryLog log = new HistoryLog();
        log.setUser(user);
        log.setCommandType("REMOVE");
        log.setSongId(songId);
        log.setTitle(songToDelete.getTitle());
        log.setArtist(songToDelete.getArtist());
        log.setPlaylistId(songToDelete.getPlaylistId());
        log.setRating(songToDelete.getRating());
        log.setSharedCatalogId(songToDelete.getId());
        log.setStatus("executed");
        historyRepository.save(log);
        sharedRepository.delete(songToDelete);

        triggerJsonSync(user);
    }


    public MergeResponseDto mergeFromFriendStream(User user, String playlistId, InputStream inputStream) {
        int addedCount = 0;
        int skippedCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("{\"type\"") && line.contains("\"ADD\"")) {
                    String title = journalService.extractField(line, "title");
                    String artist = journalService.extractField(line, "artist");
                    int rating = Integer.parseInt(journalService.extractField(line, "rating"));

                    boolean exists = sharedRepository.existsByPlaylistIdAndTitleAndArtist(playlistId, title, artist);

                    if (!exists) {
                        AddSongRequestDto dto = new AddSongRequestDto(playlistId, title, artist, rating);
                        addSong(user, dto);
                        addedCount++;
                    } else {
                        skippedCount++;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Merging error: " + e.getMessage());
        }

        return new MergeResponseDto(addedCount, skippedCount, "Merging successfully executed!");
    }


    private void triggerJsonSync(User user) {
        List<HistoryLog> allHistory = historyRepository.findByUser(user);
        List<HistoryLog> executed = allHistory.stream().filter(l -> l.getStatus().equals("executed")).collect(Collectors.toList());
        List<HistoryLog> undone = allHistory.stream().filter(l -> l.getStatus().equals("undone")).collect(Collectors.toList());

        journalService.writeHistoryToJsonFile(executed, undone);
    }
}