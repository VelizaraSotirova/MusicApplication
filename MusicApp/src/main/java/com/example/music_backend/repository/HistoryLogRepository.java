package com.example.music_backend.repository;

import com.example.music_backend.model.HistoryLog;
import com.example.music_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryLogRepository extends JpaRepository<HistoryLog, String> {
    List<HistoryLog> findByUser(User user);
    List<HistoryLog> findByUserAndStatus(User user, String executed);
    HistoryLog findTopByStatusOrderByCreatedAtDesc(String status);
}
