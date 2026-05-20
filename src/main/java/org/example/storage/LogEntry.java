package org.example.storage;


public class LogEntry {

    public final String timestamp;
    public final String source;
    public final String eventType;
    public final String action;    // może być pusty ("") gdy wpis to samo INFO

    public LogEntry(String timestamp, String source, String eventType, String action) {
        this.timestamp = timestamp;
        this.source    = source;
        this.eventType = eventType;
        this.action    = action == null ? "" : action;
    }

    @Override
    public String toString() {
        if (action.isEmpty()) {
            return "[" + timestamp + "] INFO  | " + source + " → " + eventType;
        } else {
            return "[" + timestamp + "] AKCJA | " + action;
        }
    }
}
