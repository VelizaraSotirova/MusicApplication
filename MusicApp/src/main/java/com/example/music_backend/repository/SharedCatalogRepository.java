package com.example.music_backend.repository;

import com.example.music_backend.model.SharedCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedCatalogRepository extends JpaRepository<SharedCatalog, String> {
    List<SharedCatalog> findByPlaylistId(String playlistId);
    boolean existsByPlaylistIdAndTitleAndArtist(String playlistId, String title, String artist);
}
