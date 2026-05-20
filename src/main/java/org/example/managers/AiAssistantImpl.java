package org.example.managers;

import org.example.interfaces.IObserver;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.models.*;
import org.example.storage.JsonLogStorage;
import org.example.storage.LogEntry;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class AiAssistantImpl implements AiAssistant, IObserver {

    //Podsystemy
    private final Camera camera;
    private final Heating heating;
    private final Light light;
    private final AirIonization ionization;
    private final SolarPanel solarPanel;
    private final Speakers speakers;
    private final Washing washing;

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


    public AiAssistantImpl(String logFilePath) {
        // Tworzenie podsystemów
        camera = new Camera();
        heating = new Heating();
        light = new Light();
        ionization = new AirIonization();
        solarPanel = new SolarPanel(0);
        speakers = new Speakers();
        washing = new Washing();

        storage = new JsonLogStorage(logFilePath);

        // Rejestracja obserwatora na końcu konstruktora — wszystkie pola już zainicjalizowane
        camera.addObserver(this);
        heating.addObserver(this);
        washing.addObserver(this);
    }

    //Wlaczanie urzadzenia
    @Override
    public void turnOn(ObslugaFunkcjonalnosciDomu device)  { device.turnOn();          }

    @Override
    public void turnOff(ObslugaFunkcjonalnosciDomu device) { device.turnOff();         }

    @Override
    public boolean isActive(ObslugaFunkcjonalnosciDomu device) { return device.checkStatus(); }

    //Dostep do urzadzen
    public Camera getCamera(){ return camera;}
    public Heating getHeating(){ return heating;}
    public Light getLight(){ return light;}
    public AirIonization getIonization(){ return ionization;}
    public SolarPanel getSolarPanel(){ return solarPanel;}
    public Speakers getSpeakers(){ return speakers;}
    public Washing getWashing(){ return washing;}

    //Lista urzadzen sterowalnych — uzywana np. w trybie awaryjnym
    public List<ObslugaFunkcjonalnosciDomu> getAllDevices() {
        return List.of(camera, heating, light, ionization, speakers, washing);
    }

    //!!!SYMULACJE!!!
    @Override
    public void simulateCameraMotion() {
        camera.motionDetection();
    }

    @Override
    public void simulateLowTemperature() {
        float original = heating.getTemperature();
        heating.setTemperature(10.0f);
        heating.temperatureDetection();
        heating.setTemperature(original);
    }

    @Override
    public void simulateWashingFinished() {
        washing.setFinished(true);
        washing.isWashingFinished();
        washing.setFinished(false);
    }

    //logi
    @Override
    public List<String> getSessionLog(){ return sessionLog;}

    @Override
    public List<LogEntry> getFullLog(){return storage.load();}

    @Override
    public void clearLog(){ storage.clear(); }

    //wywolane observer
    @Override
    public void reagujNaZdarzenie(String source, String eventType) {
        String time = LocalTime.now().format(TIME_FMT);
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
}
