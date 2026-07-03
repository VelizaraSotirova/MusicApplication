package com.example.music_backend.service;

import com.example.music_backend.model.HistoryLog;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.List;

@Service
public class JsonJournalService {

    private final String jsonFilePath = "catalog_journal.json";


    public void writeHistoryToJsonFile(List<HistoryLog> executed, List<HistoryLog> undone) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(jsonFilePath))) {
            writer.println("{");
            writer.println("  \"executed\": [");
            for (int i = 0; i < executed.size(); i++) {
                HistoryLog h = executed.get(i);
                writer.printf("    {\"type\": \"%s\", \"id\": \"%s\", \"title\": \"%s\", \"artist\": \"%s\", \"rating\": %d}%s\n",
                        h.getCommandType(), h.getSongId(), h.getTitle(), h.getArtist(), h.getRating(), (i == executed.size() - 1) ? "" : ",");
            }
            writer.println("  ],");
            writer.println("  \"undone\": [");
            for (int i = 0; i < undone.size(); i++) {
                HistoryLog h = undone.get(i);
                writer.printf("    {\"type\": \"%s\", \"id\": \"%s\", \"title\": \"%s\", \"artist\": \"%s\", \"rating\": %d}%s\n",
                        h.getCommandType(), h.getSongId(), h.getTitle(), h.getArtist(), h.getRating(), (i == undone.size() - 1) ? "" : ",");
            }
            writer.println("  ]");
            writer.println("}");
        } catch (IOException e) {
            System.out.println("Exception writing to JSON: " + e.getMessage());
        }
    }


    public String extractField(String line, String field) {
        int index = line.indexOf("\"" + field + "\":");
        if (index == -1) return "";
        int start = line.indexOf(":", index) + 1;
        int end = line.indexOf(",", start);
        if (end == -1) end = line.indexOf("}", start);
        return line.substring(start, end).trim().replace("\"", "");
    }
}