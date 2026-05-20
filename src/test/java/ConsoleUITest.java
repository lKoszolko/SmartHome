import org.example.ConsoleUI;
import org.example.managers.SmartHomeManager;
import org.example.models.Inhabitant;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;


public class ConsoleUITest {

    private static final int    CORRECT_PIN = 1234;
    private static final int    WRONG_PIN   = 0000;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream           originalOut = System.out;
    private final InputStream           originalIn  = System.in;

    // ── Setup / Teardown ──────────────────────────────────────────────────────

    @BeforeEach
    void redirectOutput() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ConsoleUI buildUI(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        SmartHomeManager manager = new SmartHomeManager(new Inhabitant(true, CORRECT_PIN));
        return new ConsoleUI(manager);
    }

    private String output() {
        return outContent.toString();
    }

    // ── Autoryzacja ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Błędny PIN blokuje dostęp do systemu")
    void wrongPinBlocksAccess() {
        System.setIn(new ByteArrayInputStream((WRONG_PIN + "\n").getBytes()));
        SmartHomeManager manager = new SmartHomeManager(new Inhabitant(true, CORRECT_PIN));
        new ConsoleUI(manager).start();

        assertTrue(output().contains("Dostęp zablokowany"),
                "Powinien pojawić się komunikat o zablokowanym dostępie");
    }

    @Test
    @DisplayName("Poprawny PIN wpuszcza do menu głównego")
    void correctPinShowsMainMenu() {
        buildUI(CORRECT_PIN + "\n5\n").start();

        assertTrue(output().contains("MENU GŁÓWNE"),
                "Menu główne powinno być widoczne po zalogowaniu");
    }

    @Test
    @DisplayName("Poprawny PIN wyświetla komunikat o autoryzacji")
    void correctPinShowsSuccessMessage() {
        buildUI(CORRECT_PIN + "\n5\n").start();

        assertTrue(output().contains("Autoryzacja pomyślna"),
                "Powinien pojawić się komunikat o udanej autoryzacji");
    }

    // ── Menu urządzeń ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Lista urządzeń wyświetla wszystkie urządzenia")
    void devicesMenuShowsAllDevices() {
        // login → urządzenia → wróć → wyjście
        buildUI(CORRECT_PIN + "\n1\n7\n5\n").start();

        String out = output();
        assertTrue(out.contains("Kamera"),             "Brak: Kamera");
        assertTrue(out.contains("Ogrzewanie"),         "Brak: Ogrzewanie");
        assertTrue(out.contains("Światło"),            "Brak: Światło");
        assertTrue(out.contains("Jonizacja"),          "Brak: Jonizacja powietrza");
        assertTrue(out.contains("Głośniki"),           "Brak: Głośniki");
        assertTrue(out.contains("Pralka"),             "Brak: Pralka");
    }

    @Test
    @DisplayName("Kamera domyślnie ma status OFF")
    void cameraDefaultStatusIsOff() {
        buildUI(CORRECT_PIN + "\n1\n7\n5\n").start();

        // Sprawdzamy wiersz z kamerą — powinien zawierać [OFF]
        String line = output().lines()
                .filter(l -> l.contains("Kamera"))
                .findFirst().orElse("");
        assertTrue(line.contains("OFF"), "Kamera powinna być domyślnie wyłączona");
    }

    // ── Włączanie / Wyłączanie urządzeń ──────────────────────────────────────

    @Test
    @DisplayName("Włączenie kamery zmienia jej status na ON")
    void turnOnCameraChangesStatus() {
        // login → urządzenia → kamera → włącz → wróć → wróć → wyjście
        buildUI(CORRECT_PIN + "\n1\n1\n1\n2\n7\n5\n").start();

        assertTrue(output().contains("[OK] Urządzenie włączone"),
                "Powinien pojawić się komunikat o włączeniu");
    }

    @Test
    @DisplayName("Wyłączenie głośników — komunikat o wyłączeniu")
    void turnOffSpeakersShowsMessage() {
        // Włącz, potem wyłącz
        buildUI(CORRECT_PIN + "\n1\n5\n1\n1\n2\n7\n5\n").start();

        String out = output();
        assertTrue(out.contains("[OK] Urządzenie włączone"),  "Brak komunikatu włączenia");
        assertTrue(out.contains("[OK] Urządzenie wyłączone"), "Brak komunikatu wyłączenia");
    }

    // ── Ogrzewanie ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Ustawienie temperatury ogrzewania wyświetla potwierdzenie")
    void setHeatingTemperatureShowsConfirmation() {
        // login → urządzenia → ogrzewanie → ustaw temp → 22.5 → wróć → wróć → wyjście
        buildUI(CORRECT_PIN + "\n1\n2\n2\n22.5\n3\n7\n5\n").start();

        assertTrue(output().contains("22.5"),
                "Nowa temperatura powinna pojawić się w komunikacie");
    }

    @Test
    @DisplayName("Podanie liter zamiast temperatury wyświetla błąd")
    void invalidTemperatureInputShowsError() {
        // login → urządzenia → ogrzewanie → ustaw temp → abc → wróć → wróć → wyjście
        buildUI(CORRECT_PIN + "\n1\n2\n2\nabc\n3\n7\n5\n").start();

        assertTrue(output().contains("[!!]"),
                "Nieprawidłowy input powinien wygenerować komunikat błędu");
    }

    // ── Światło ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Ustawienie jasności po włączeniu działa poprawnie")
    void setLightBrightnessAfterTurnOn() {
        // login → urządzenia → światło → włącz → ustaw jasność → 80 → wróć → wróć → wyjście
        buildUI(CORRECT_PIN + "\n1\n3\n1\n2\n80\n3\n7\n5\n").start();

        assertTrue(output().contains("80"),
                "Poziom jasności 80 powinien pojawić się w komunikacie");
    }

    @Test
    @DisplayName("Jasność poza zakresem (>100) wyświetla błąd")
    void brightnessOutOfRangeShowsError() {
        // login → urządzenia → światło → włącz → ustaw jasność → 150 → wróć → wróć → wyjście
        buildUI(CORRECT_PIN + "\n1\n3\n1\n2\n150\n3\n7\n5\n").start();

        assertTrue(output().contains("[!!]"),
                "Jasność > 100 powinna wygenerować błąd");
    }

    // ── Sensory ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Menu sensorów wyświetla panel słoneczny i jonizację")
    void sensorsMenuShowsBothSensors() {
        // login → sensory → wróć → wyjście
        buildUI(CORRECT_PIN + "\n2\n2\n5\n").start();

        String out = output();
        assertTrue(out.contains("Panel"),    "Brak panelu słonecznego w sensorach");
        assertTrue(out.contains("Jonizacja"),"Brak jonizacji w sensorach");
    }

    @Test
    @DisplayName("Ustawienie energii panelu słonecznego wyświetla potwierdzenie")
    void setSolarPanelEnergyShowsConfirmation() {
        // login → sensory → ustaw energię → 5.5 → wróć → wyjście
        buildUI(CORRECT_PIN + "\n2\n1\n5.5\n5\n").start();

        assertTrue(output().contains("5.5"),
                "Wartość energii powinna pojawić się w potwierdzeniu");
    }

    // ── Symulacja zdarzeń ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Symulacja ruchu kamery wyzwala odpowiedź AI")
    void simulateCameraMotionTriggersAI() {
        // login → symulacja → kamera → enter → wróć → wyjście
        buildUI(CORRECT_PIN + "\n3\n1\n\n4\n5\n").start();

        String out = output();
        assertTrue(out.contains("Kamera"),  "Zdarzenie powinno pochodzić z Kamery");
        assertTrue(out.contains("Ruch"),    "Typ zdarzenia powinien być 'Ruch'");
    }

    @Test
    @DisplayName("Symulacja niskiej temperatury wyzwala odpowiedź AI")
    void simulateLowTemperatureTriggersAI() {
        // login → symulacja → niska temp → enter → wróć → wyjście
        buildUI(CORRECT_PIN + "\n3\n2\n\n4\n5\n").start();

        assertTrue(output().contains("Niska temperatura"),
                "AI powinien zareagować na niską temperaturę");
    }

    @Test
    @DisplayName("Symulacja zakończenia prania wyzwala odpowiedź AI")
    void simulateWashingFinishedTriggersAI() {
        // login → symulacja → pranie → enter → wróć → wyjście
        buildUI(CORRECT_PIN + "\n3\n3\n\n4\n5\n").start();

        assertTrue(output().contains("Pranie"),
                "AI powinien zareagować na zakończenie prania");
    }

    // ── Log AI ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Pusty log AI informuje o braku zdarzeń")
    void emptyAiLogShowsNoEventsMessage() {
        // login → log AI → enter → wyjście (bez symulacji)
        buildUI(CORRECT_PIN + "\n4\n\n5\n").start();

        assertTrue(output().contains("brak zdarzeń"),
                "Pusty log powinien informować o braku zdarzeń");
    }

    @Test
    @DisplayName("Po symulacji log AI zawiera zapis zdarzenia")
    void aiLogContainsEventAfterSimulation() {
        // login → symulacja → kamera → enter → wróć → AI log → enter → wyjście
        buildUI(CORRECT_PIN + "\n3\n1\n\n4\n\n5\n").start();

        String out = output();
        assertTrue(out.contains("INFO"),   "Log powinien zawierać wpis INFO");
        assertTrue(out.contains("Kamera"), "Log powinien zawierać nazwę urządzenia");
    }

    @Test
    @DisplayName("Log AI zawiera timestamp w formacie HH:mm:ss")
    void aiLogContainsTimestamp() {
        // login → symulacja → kamera → enter → wróć → AI log → enter → wyjście
        buildUI(CORRECT_PIN + "\n3\n1\n\n4\n\n5\n").start();

        // Timestamp wygląda jak [HH:mm:ss]
        assertTrue(output().matches("(?s).*\\[\\d{2}:\\d{2}:\\d{2}\\].*"),
                "Log powinien zawierać timestamp w formacie [HH:mm:ss]");
    }

    // ── Obsługa błędnych inputów ──────────────────────────────────────────────

    @Test
    @DisplayName("Nieznana opcja w menu głównym wyświetla błąd")
    void unknownMainMenuOptionShowsError() {
        // login → nieznana opcja → wyjście
        buildUI(CORRECT_PIN + "\n9\n5\n").start();

        assertTrue(output().contains("[!!]"),
                "Nieznana opcja powinna wyświetlić komunikat błędu");
    }

    @Test
    @DisplayName("Nieznana opcja w menu urządzeń wyświetla błąd")
    void unknownDeviceMenuOptionShowsError() {
        // login → urządzenia → nieznana opcja → wróć → wyjście
        buildUI(CORRECT_PIN + "\n1\n9\n7\n5\n").start();

        assertTrue(output().contains("[!!]"),
                "Nieznana opcja w urządzeniach powinna wyświetlić błąd");
    }
}
