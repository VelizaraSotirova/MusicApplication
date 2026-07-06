package com.example.music_backend.service;

import com.example.music_backend.model.HistoryLog;
import com.example.music_backend.model.SharedCatalog;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonJournalService {

    private final String BASE_DIRECTORY = System.getProperty("user.home") + "/music_catalogs/";


    public void writeHistoryToJsonFile(String username, List<HistoryLog> executed, List<HistoryLog> undone) {
        String directoryPath = System.getProperty("user.home") + File.separator + "music_catalogs";
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, username + "_songs.json");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("{");

            writer.println("  \"executed\": [");
            for (int i = 0; i < executed.size(); i++) {
                HistoryLog h = executed.get(i);
                writer.printf("    {\"type\": \"%s\", \"id\": \"%s\", \"title\": \"%s\", \"artist\": \"%s\", \"rating\": %d}%s\n",
                        h.getCommandType(), h.getSongId(), h.getTitle(), h.getArtist(), h.getRating(),
                        (i == executed.size() - 1) ? "" : ",");
            }
            writer.println("  ],");

            writer.println("  \"undone\": [");
            for (int i = 0; i < undone.size(); i++) {
                HistoryLog h = undone.get(i);
                writer.printf("    {\"type\": \"%s\", \"id\": \"%s\", \"title\": \"%s\", \"artist\": \"%s\", \"rating\": %d}%s\n",
                        h.getCommandType(), h.getSongId(), h.getTitle(), h.getArtist(), h.getRating(),
                        (i == undone.size() - 1) ? "" : ",");
            }
            writer.println("  ]");
            writer.println("}");

            System.out.println("Файлът е записан успешно: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Грешка при запис на JSON: " + e.getMessage());
        }
    }

    public List<SharedCatalog> readCatalogFromFile(String username) {
        List<SharedCatalog> catalog = new ArrayList<>();
        String filePath = BASE_DIRECTORY + username + "_songs.json";
        File file = new File(filePath);

        if (!file.exists()) return catalog;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean insideExecuted = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.contains("\"executed\":")) insideExecuted = true;
                if (line.startsWith("],")) insideExecuted = false;

                if (insideExecuted && line.startsWith("{")) {
                    SharedCatalog song = new SharedCatalog();
                    song.setSongId(extractField(line, "id"));
                    song.setTitle(extractField(line, "title"));
                    song.setArtist(extractField(line, "artist"));
                    song.setRating(Integer.parseInt(extractField(line, "rating")));

                    catalog.add(song);
                }
            }
        } catch (IOException e) {
            System.err.println("Грешка при четене: " + e.getMessage());
        }
        return catalog;
    }

    public String extractField(String line, String field) {
        String searchKey = "\"" + field + "\":";
        int index = line.indexOf(searchKey);
        if (index == -1) return "";

        int start = index + searchKey.length();

        if (line.contains("\"" + field + "\": \"")) {
            start = line.indexOf("\"", start) + 1;
            int end = line.indexOf("\"", start);
            return line.substring(start, end);
        } else {
            int end = line.indexOf(",", start);
            if (end == -1) end = line.indexOf("}", start);
            return line.substring(start, end).trim();
        }
    }
}