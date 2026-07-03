package com.example.music_backend.repository;

import com.example.music_backend.model.HistoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryLogRepository extends JpaRepository<HistoryLog, String> {
}
