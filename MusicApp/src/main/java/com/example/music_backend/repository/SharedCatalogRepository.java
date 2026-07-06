package com.example.music_backend.repository;

import com.example.music_backend.model.SharedCatalog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface SharedCatalogRepository extends JpaRepository<SharedCatalog, String> {
    boolean existsByPlaylistIdAndTitleAndArtist(String playlistId, String title, String artist);
    List<SharedCatalog> findByAddedByUsername(String username);
    Optional<SharedCatalog> findBySongIdAndAddedByUsername(String songId, String username);

    @Modifying
    @Transactional
    void deleteBySongIdAndAddedByUsername(String songId, String addedByUsername);

    @Modifying
    @Transactional
    void deleteBySongId(String songId);
}
