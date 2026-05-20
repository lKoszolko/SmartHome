package org.example.storage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class JsonLogStorage {

    private final String filePath;

    public JsonLogStorage(String filePath) {
        this.filePath = filePath;
    }

    //zapis
    public void append(LogEntry entry) {
        List<LogEntry> all = load();
        all.add(entry);
        save(all);
    }

    public void save(List<LogEntry> entries) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < entries.size(); i++) {
            LogEntry e = entries.get(i);
            sb.append("{\n");
            sb.append("\"timestamp\": \"").append(escape(e.timestamp)).append("\",\n");
            sb.append("\"source\": \"").append(escape(e.source)).append("\",\n");
            sb.append("\"eventType\": \"").append(escape(e.eventType)).append("\",\n");
            sb.append("\"action\": \"").append(escape(e.action)).append("\"\n");
            sb.append("  }");
            if (i < entries.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");

        try {
            Files.writeString(Path.of(filePath), sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.err.println("[LOG] Błąd zapisu do " + filePath + ": " + ex.getMessage());
        }
    }

    //odczyt
    public List<LogEntry> load() {
        List<LogEntry> result = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return result;

        try {
            String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            parseJson(content, result);
        } catch (IOException ex) {
            System.err.println("[LOG] Błąd odczytu " + filePath + ": " + ex.getMessage());
        }
        return result;
    }

    public void clear() {
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException ex) {
            System.err.println("[LOG] Błąd usuwania " + filePath + ": " + ex.getMessage());
        }
    }

    //parser do jsona(Logi)
    private void parseJson(String content, List<LogEntry> out) {
        // Każdy obiekt to blok { ... }
        int i = 0;
        while ((i = content.indexOf('{', i)) != -1) {
            int end = content.indexOf('}', i);
            if (end == -1) break;
            String obj = content.substring(i, end + 1);
            out.add(parseObject(obj));
            i = end + 1;
        }
    }

    private LogEntry parseObject(String obj) {
        return new LogEntry(
                extractField(obj, "timestamp"),
                extractField(obj, "source"),
                extractField(obj, "eventType"),
                extractField(obj, "action")
        );
    }

    private String extractField(String obj, String key) {
        String search = "\"" + key + "\": \"";
        int start = obj.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = obj.indexOf('"', start);
        if (end == -1) return "";
        return unescape(obj.substring(start, end));
    }

    //funckje pomocnicze
    private String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescape(String s) {
        return s.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }
}
