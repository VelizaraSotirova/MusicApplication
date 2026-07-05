package com.example.music_backend.repository;

import com.example.music_backend.model.SharedCatalog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface SharedCatalogRepository extends JpaRepository<SharedCatalog, String> {
    boolean existsByPlaylistIdAndTitleAndArtist(String playlistId, String title, String artist);

    @Modifying
    @Transactional
    void deleteBySongIdAndAddedByUsername(String songId, String addedByUsername);
}
