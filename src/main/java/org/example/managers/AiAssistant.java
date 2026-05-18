package org.example.managers;

import org.example.interfaces.IObserver;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.interfaces.Sensors;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AiAssistant implements IObserver {
    private List<ObslugaFunkcjonalnosciDomu> devices = new ArrayList<>();
    private List<Sensors> sensors = new ArrayList<>();
    private String currentMode;

    /** historia zdarzeń i mapowanie logow, zeby nie wygladaly informatycznie */
    private final List<String> eventLog = new ArrayList<>();

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final java.util.Map<String, String> DEVICE_NAMES = java.util.Map.of(
        "class org.example.models.Camera",        "Kamera",
        "class org.example.models.Heating",       "Ogrzewanie",
        "class org.example.models.Light",         "Swiatlo",
        "class org.example.models.AirIonization", "Jonizacja powietrza",
        "class org.example.models.Washing",       "Pralka",
        "class org.example.models.Speakers",      "Glosniki",
        "class org.example.models.SolarPanel",    "Panel sloneczny"
    );

    private String friendlyName(String source) {
        return DEVICE_NAMES.getOrDefault(source, source);
    }

    public AiAssistant(List<ObslugaFunkcjonalnosciDomu> devices) {
        this.devices = devices;
    }
    public AiAssistant() { }

    // ── IObserver ─────────────────────────────────────────────────────────────
    @Override
    public void reagujNaZdarzenie(String source, String eventType) {
        String time = LocalTime.now().format(TIME_FMT);
        String info = "[" + time + "] INFO  | Zdarzenie z [" + friendlyName(source) + "] → " + eventType;
        eventLog.add(info);
        System.out.println("AI " + info);

        if (source.contains("Camera") && eventType.equals("Ruch")) {
            String action = "[" + time + "] AKCJA | Włączam światło i wysyłam powiadomienie!";
            eventLog.add(action);
            System.out.println("AI " + action);
        }
        if (source.contains("Heating") && eventType.equals("Niska temperatura")) {
            String action = "[" + time + "] AKCJA | Podnoszę temperaturę ogrzewania.";
            eventLog.add(action);
            System.out.println("AI " + action);
        }
        if (source.contains("Washing") && eventType.equals("Pranie skonczone")) {
            String action = "[" + time + "] AKCJA | Wysyłam powiadomienie: pranie gotowe!";
            eventLog.add(action);
            System.out.println("AI " + action);
        }
    }

    // ── Zarządzanie urządzeniami ──────────────────────────────────────────────
    public void addDevice(ObslugaFunkcjonalnosciDomu device) { devices.add(device); }
    public void turnOffDevice(ObslugaFunkcjonalnosciDomu o)  { o.turnOff(); }
    public void turnOnDevice(ObslugaFunkcjonalnosciDomu o)   { o.turnOn();  }

    public List<ObslugaFunkcjonalnosciDomu> getDevices() { return devices; }
    public List<String> getEventLog()                    { return eventLog; }

    public void printActiveDevices() {
        devices.stream()
               .filter(ObslugaFunkcjonalnosciDomu::checkStatus)
               .forEach(System.out::println);
    }
}
