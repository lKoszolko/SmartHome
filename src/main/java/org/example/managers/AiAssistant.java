package org.example.managers;

import org.example.interfaces.IObserver;
import org.example.models.*;
import org.example.storage.JsonLogStorage;
import org.example.storage.LogEntry;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class AiAssistant implements IObserver {

    //Podsystemy
    private final Camera       camera;
    private final Heating      heating;
    private final Light        light;
    private final AirIonization ionization;
    private final SolarPanel   solarPanel;
    private final Speakers     speakers;
    private final Washing      washing;

    // ── Log ───────────────────────────────────────────────────────────────────
    private final List<String> sessionLog = new ArrayList<>();   // log bieżącej sesji
    private final JsonLogStorage storage;

    //mapowanie clas na nazwy
    private static final Map<String, String> DEVICE_NAMES = Map.of(
        "class org.example.models.Camera",        "Kamera",
        "class org.example.models.Heating",       "Ogrzewanie",
        "class org.example.models.Light",         "Swiatlo",
        "class org.example.models.AirIonization", "Jonizacja powietrza",
        "class org.example.models.Washing",       "Pralka",
        "class org.example.models.Speakers",      "Glosniki",
        "class org.example.models.SolarPanel",    "Panel sloneczny"
    );

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    // ── Konstruktor ───────────────────────────────────────────────────────────

    public AiAssistant(String logFilePath) {
        // Tworzenie podsystemów
        camera     = new Camera();
        heating    = new Heating();
        light      = new Light();
        ionization = new AirIonization();
        solarPanel = new SolarPanel(0);
        speakers   = new Speakers();
        washing    = new Washing();

        storage = new JsonLogStorage(logFilePath);

        // Rejestracja obserwatora na końcu konstruktora — wszystkie pola już zainicjalizowane
        camera.addObserver(this);
        heating.addObserver(this);
        washing.addObserver(this);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // INTERFEJS FASADY — jedyne metody dostępne z zewnątrz
    // ═════════════════════════════════════════════════════════════════════════

    // ── Włącz / Wyłącz ────────────────────────────────────────────────────────

    public void turnOn(DeviceName d)  { resolve(d).turnOn();  }
    public void turnOff(DeviceName d) { resolve(d).turnOff(); }

    // ── Status ────────────────────────────────────────────────────────────────

    public boolean isActive(DeviceName d) { return resolve(d).checkStatus(); }

    // ── Ogrzewanie ────────────────────────────────────────────────────────────

    public void  setTemperature(float t) { heating.setTemperature(t); }
    public float getTemperature()        { return heating.getTemperature(); }

    // ── Światło ───────────────────────────────────────────────────────────────

    public void setBrightness(int level) { light.changeLevel(level); }
    public int  getBrightness()          { return light.getBrightnessLevel(); }

    // ── Sensory ───────────────────────────────────────────────────────────────

    public float readIonization()            { return ionization.readValue(); }
    public float readSolarEnergy()           { return solarPanel.readValue(); }
    public void  setSolarEnergy(float value) { solarPanel.setEnergy(value); }

    // ── Symulacja zdarzeń ─────────────────────────────────────────────────────

    public void simulateCameraMotion() {
        camera.motionDetection();
    }

    public void simulateLowTemperature() {
        float original = heating.getTemperature();
        heating.setTemperature(10.0f);
        heating.temperatureDetection();
        heating.setTemperature(original);
    }

    public void simulateWashingFinished() {
        washing.setFinished(true);
        washing.isWashingFinished();
        washing.setFinished(false);
    }

    // ── Logi ──────────────────────────────────────────────────────────────────

    public List<String> getSessionLog() { return sessionLog; }

    public List<LogEntry> getFullLog() { return storage.load(); }

    public void clearLog() { storage.clear(); }

    // ═════════════════════════════════════════════════════════════════════════
    // IObserver — wywoływane przez podsystemy (wewnętrzne)
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void reagujNaZdarzenie(String source, String eventType) {
        String time    = LocalTime.now().format(TIME_FMT);
        String friendly = DEVICE_NAMES.getOrDefault(source, source);

        // Wpis INFO
        String infoMsg = "[" + time + "] INFO  | Zdarzenie z [" + friendly + "] -> " + eventType;
        sessionLog.add(infoMsg);
        storage.append(new LogEntry(time, friendly, eventType, ""));
        System.out.println("AI " + infoMsg);

        // Reakcja + wpis AKCJA
        String action = determineAction(source, eventType);
        if (action != null) {
            String actionMsg = "[" + time + "] AKCJA | " + action;
            sessionLog.add(actionMsg);
            storage.append(new LogEntry(time, friendly, eventType, action));
            System.out.println("AI " + actionMsg);
        }
    }

    private String determineAction(String source, String eventType) {
        if (source.contains("Camera") && eventType.equals("Ruch"))
            return "Wlaczam swiatlo i wysylam powiadomienie!";
        if (source.contains("Heating") && eventType.equals("Niska temperatura"))
            return "Podniosze temperature ogrzewania.";
        if (source.contains("Washing") && eventType.equals("Pranie skonczone"))
            return "Wysylam powiadomienie: pranie gotowe!";
        return null;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ENUM nazw urządzeń — uniemożliwia literówki, IDE podpowiada
    // ═════════════════════════════════════════════════════════════════════════

    public enum DeviceName {
        KAMERA, OGRZEWANIE, SWIATLO, JONIZACJA, GLOSNIKI, PRALKA
    }

    // ── Wewnętrzne mapowanie DeviceName → instancja urządzenia ───────────────

    private org.example.interfaces.ObslugaFunkcjonalnosciDomu resolve(DeviceName d) {
        return switch (d) {
            case KAMERA     -> camera;
            case OGRZEWANIE -> heating;
            case SWIATLO    -> light;
            case JONIZACJA  -> ionization;
            case GLOSNIKI   -> speakers;
            case PRALKA     -> washing;
        };
    }
}
