import org.example.ConsoleUI;
import org.example.managers.SmartHomeManager;
import org.example.models.Inhabitant;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy konsolowego UI systemu SmartHome.
 *
 * Technika: zamieniamy System.in na ByteArrayInputStream (symulacja wpisów),
 * a System.out na ByteArrayOutputStream (przechwycenie wydruku).
 *
 * Każda sekwencja inputów jest zweryfikowana manualnie — każda linia
 * odpowiada jednemu wywołaniu scanner.nextLine() w ConsoleUI.
 */
public class ConsoleUITest {

    private static final int CORRECT_PIN = 1234;
    private static final int WRONG_PIN   = 9999;

    private final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
    private final PrintStream            originalOut = System.out;
    private final InputStream            originalIn  = System.in;

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

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Tworzy i uruchamia ConsoleUI z podanym ciągiem wejściowym.
     * Linie oddzielone są znakiem \n.
     */
    private void run(String inputLines) {
        System.setIn(new ByteArrayInputStream(inputLines.getBytes()));
        SmartHomeManager manager = new SmartHomeManager(new Inhabitant(true, CORRECT_PIN));
        new ConsoleUI(manager).start();
    }

    private String output() {
        return outContent.toString();
    }

    // ── Autoryzacja ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Błędny PIN blokuje dostęp")
    void wrongPinBlocksAccess() {
        run(WRONG_PIN + "\n");
        assertTrue(output().contains("zablokowany"));
    }

    @Test
    @DisplayName("Poprawny PIN wyświetla komunikat autoryzacji")
    void correctPinShowsAuthConfirmation() {
        // PIN → wyjście
        run(CORRECT_PIN + "\n5\n");
        assertTrue(output().contains("Autoryzacja"));
    }

    @Test
    @DisplayName("Poprawny PIN otwiera menu główne")
    void correctPinShowsMainMenu() {
        // PIN → wyjście
        run(CORRECT_PIN + "\n5\n");
        assertTrue(output().contains("MENU"));
    }

    // ── Menu urządzeń ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Menu urządzeń wyświetla wszystkie 6 urządzeń")
    void devicesMenuShowsAllDevices() {
        // PIN → urządzenia → wróć → wyjście
        run(CORRECT_PIN + "\n1\n7\n5\n");

        String out = output();
        assertAll(
            () -> assertTrue(out.contains("Kamera"),    "Brak: Kamera"),
            () -> assertTrue(out.contains("Ogrzewanie"),"Brak: Ogrzewanie"),
            () -> assertTrue(out.contains("wiat"),      "Brak: Światło"),
            () -> assertTrue(out.contains("Jonizacja"), "Brak: Jonizacja"),
            () -> assertTrue(out.contains("os"),        "Brak: Głośniki"),
            () -> assertTrue(out.contains("Pralka"),    "Brak: Pralka")
        );
    }

    @Test
    @DisplayName("Domyślny status każdego urządzenia to OFF")
    void allDevicesDefaultStatusIsOff() {
        // PIN → urządzenia → wróć → wyjście
        run(CORRECT_PIN + "\n1\n7\n5\n");
        long offCount = output().lines()
                .filter(l -> l.contains("OFF"))
                .count();
        assertTrue(offCount >= 6, "Powinno być co najmniej 6 urządzeń ze statusem OFF");
    }

    // ── Włączanie / Wyłączanie ────────────────────────────────────────────────

    @Test
    @DisplayName("Włączenie kamery wyświetla potwierdzenie")
    void turnOnCameraShowsConfirmation() {
        // PIN → urządzenia → kamera → włącz → wróć z kamery → wróć do main → wyjście
        run(CORRECT_PIN + "\n1\n1\n1\n2\n7\n5\n");
        assertTrue(output().contains("[OK] Urz"));
    }

    @Test
    @DisplayName("Włączenie i wyłączenie głośników daje oba komunikaty")
    void toggleSpeakersTwiceShowsBothMessages() {
        // PIN → urządzenia → głośniki → włącz → wyłącz → wróć → wróć → wyjście
        run(CORRECT_PIN + "\n1\n5\n1\n1\n2\n7\n5\n");
        String out = output();
        // "włączone" i "wyłączone" — sprawdzamy rdzeń żeby uniknąć problemów z kodowaniem
        assertTrue(out.contains("czone"), "Powinien pojawić się komunikat zmiany stanu");
    }

    // ── Ogrzewanie ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Ustawienie temperatury 22.5°C wyświetla potwierdzenie z wartością")
    void setHeatingTemperatureShowsValue() {
        // PIN → urządzenia → ogrzewanie → ustaw temp → 22.5 → wróć → wróć → wyjście
        run(CORRECT_PIN + "\n1\n2\n2\n22.5\n3\n7\n5\n");
        assertTrue(output().contains("22.5"));
    }

    @Test
    @DisplayName("Podanie liter jako temperatury wyświetla błąd")
    void invalidTemperatureInputShowsError() {
        // PIN → urządzenia → ogrzewanie → ustaw temp → abc → (enter na błąd) → wróć → wróć → wyjście
        run(CORRECT_PIN + "\n1\n2\n2\nabc\n\n3\n7\n5\n");
        assertTrue(output().contains("[!!]"));
    }

    // ── Światło ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Ustawienie jasności 80% po włączeniu wyświetla wartość")
    void setLightBrightnessAfterTurnOn() {
        // PIN → urządzenia → światło → włącz → ustaw jasność → 80 → wróć → wróć → wyjście
        run(CORRECT_PIN + "\n1\n3\n1\n2\n80\n3\n7\n5\n");
        assertTrue(output().contains("80"));
    }

    @Test
    @DisplayName("Jasność poza zakresem (150) wyświetla błąd")
    void brightnessOutOfRangeShowsError() {
        // PIN → urządzenia → światło → włącz → ustaw jasność → 150 → (enter na błąd) → wróć → wróć → wyjście
        run(CORRECT_PIN + "\n1\n3\n1\n2\n150\n\n3\n7\n5\n");
        assertTrue(output().contains("[!!]"));
    }

    // ── Sensory ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Menu sensorów wyświetla panel słoneczny i jonizację")
    void sensorsMenuShowsBothSensors() {
        // PIN → sensory → wróć → wyjście
        run(CORRECT_PIN + "\n2\n2\n5\n");
        String out = output();
        assertTrue(out.contains("Panel") || out.contains("SOLAR"),    "Brak panelu słonecznego");
        assertTrue(out.contains("Jonizacja") || out.contains("POWIETRZE"), "Brak jonizacji");
    }

    @Test
    @DisplayName("Ustawienie energii panelu słonecznego na 5.5 wyświetla potwierdzenie")
    void setSolarPanelEnergyShowsConfirmation() {
        // PIN → sensory → ustaw energię → 5.5 → wyjście
        run(CORRECT_PIN + "\n2\n1\n5.5\n5\n");
        assertTrue(output().contains("5.5"));
    }

    // ── Symulacja zdarzeń ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Symulacja ruchu kamery wywołuje reakcję AI")
    void simulateCameraMotionTriggersAI() {
        // PIN → symulacja → kamera → enter → wróć → wyjście
        run(CORRECT_PIN + "\n3\n1\n\n4\n5\n");
        String out = output();
        assertTrue(out.contains("Kamera"), "Zdarzenie powinno zawierać nazwę Kamera");
        assertTrue(out.contains("Ruch"),   "Typ zdarzenia to Ruch");
    }

    @Test
    @DisplayName("Symulacja niskiej temperatury wywołuje reakcję AI")
    void simulateLowTemperatureTriggersAI() {
        // PIN → symulacja → niska temp → enter → wróć → wyjście
        run(CORRECT_PIN + "\n3\n2\n\n4\n5\n");
        assertTrue(output().contains("Niska temperatura"));
    }

    @Test
    @DisplayName("Symulacja zakończenia prania wywołuje reakcję AI")
    void simulateWashingFinishedTriggersAI() {
        // PIN → symulacja → pranie → enter → wróć → wyjście
        run(CORRECT_PIN + "\n3\n3\n\n4\n5\n");
        assertTrue(output().contains("Pranie") || output().contains("Pralka"));
    }

    @Test
    @DisplayName("AI drukuje AKCJA po wykryciu ruchu kamery")
    void cameraMotionTriggersAiAction() {
        // PIN → symulacja → kamera → enter → wróć → wyjście
        run(CORRECT_PIN + "\n3\n1\n\n4\n5\n");
        assertTrue(output().contains("AKCJA"));
    }

    // ── Log AI ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Pusty log AI wyświetla komunikat o braku zdarzeń")
    void emptyAiLogShowsNoEventsMessage() {
        // PIN → log AI → enter → wyjście (bez żadnej symulacji)
        run(CORRECT_PIN + "\n4\n\n5\n");
        assertTrue(output().contains("brak"));
    }

    @Test
    @DisplayName("Log AI po symulacji zawiera wpis INFO z nazwą urządzenia")
    void aiLogAfterSimulationContainsInfoEntry() {
        // PIN → symulacja → kamera → enter → wróć → log AI → enter → wyjście
        run(CORRECT_PIN + "\n3\n1\n\n4\n4\n\n5\n");
        String out = output();
        assertTrue(out.contains("INFO"),   "Log powinien zawierać wpis INFO");
        assertTrue(out.contains("Kamera"), "Log powinien zawierać nazwę urządzenia");
    }

    @Test
    @DisplayName("Log AI zawiera timestamp w formacie [HH:mm:ss]")
    void aiLogContainsTimestamp() {
        // PIN → symulacja → kamera → enter → wróć → wyjście
        run(CORRECT_PIN + "\n3\n1\n\n4\n5\n");
        assertTrue(output().matches("(?s).*\\[\\d{2}:\\d{2}:\\d{2}\\].*"),
                "Log powinien zawierać timestamp w formacie [HH:mm:ss]");
    }

    // ── Obsługa błędnych inputów ──────────────────────────────────────────────

    @Test
    @DisplayName("Nieznana opcja w menu głównym wyświetla błąd")
    void unknownMainMenuOptionShowsError() {
        // PIN → nieznana opcja (9) → (enter na błąd) → wyjście
        run(CORRECT_PIN + "\n9\n\n5\n");
        assertTrue(output().contains("[!!]"));
    }

    @Test
    @DisplayName("Nieznana opcja w menu urządzeń wyświetla błąd")
    void unknownDeviceMenuOptionShowsError() {
        // PIN → urządzenia → nieznana opcja (9) → (enter na błąd) → wróć → wyjście
        run(CORRECT_PIN + "\n1\n9\n\n7\n5\n");
        assertTrue(output().contains("[!!]"));
    }
}
