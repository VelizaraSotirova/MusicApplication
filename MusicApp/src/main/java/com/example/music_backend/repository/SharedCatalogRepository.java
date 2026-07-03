package com.example.music_backend.repository;

import com.example.music_backend.model.SharedCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedCatalogRepository extends JpaRepository<SharedCatalog, String> {
}
