package org.example;

import org.example.managers.AiAssistant;
import org.example.managers.SmartHomeManager;
import org.example.models.*;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private static final String LINE =
            "══════════════════════════════════════════════════";
    private static final String THIN =
            "──────────────────────────────────────────────────";

    private final SmartHomeManager manager;
    private final Scanner scanner;

    public ConsoleUI(SmartHomeManager manager) {
        this.manager = manager;
        this.scanner = new Scanner(System.in);
    }

    // ── Start ─────────────────────────────────────────────────────────────────

    public void start() {
        clearScreen();
        printBanner();

        if (!loginScreen()) {
            System.out.println("\n  [!!] Błędny PIN. Dostęp zablokowany.\n");
            return;
        }

        manager.initialize();
        System.out.println("\n  [OK] Autoryzacja pomyślna. System uruchomiony.\n");
        pause(1000);

        mainMenu();
        System.out.println("\n  Do widzenia!\n");
    }

    // ── Logowanie ─────────────────────────────────────────────────────────────

    private boolean loginScreen() {
        clearScreen();
        printBanner();
        System.out.println("  Podaj PIN aby się zalogować:");
        System.out.print("  > ");
        try {
            int pin = Integer.parseInt(scanner.nextLine().trim());
            return manager.authorize(pin);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ── Menu główne ───────────────────────────────────────────────────────────

    private void mainMenu() {
        boolean running = true;
        while (running) {
            clearScreen();
            printBanner();
            System.out.println("  MENU GŁÓWNE");
            System.out.println(THIN);
            System.out.println("  1. Zarządzaj urządzeniami");
            System.out.println("  2. Odczyt sensorów");
            System.out.println("  3. Symuluj zdarzenie");
            System.out.println("  4. Log AI Asystenta");
            System.out.println("  5. Wyjście");
            System.out.println(LINE);
            System.out.print("  Wybierz opcję: ");

            switch (readLine()) {
                case "1" -> devicesMenu();
                case "2" -> sensorsMenu();
                case "3" -> simulateMenu();
                case "4" -> aiLogMenu();
                case "5" -> running = false;
                default  -> showError("Nieznana opcja.");
            }
        }
    }

    // ── Urządzenia ────────────────────────────────────────────────────────────

    private void devicesMenu() {
        boolean back = false;
        while (!back) {
            clearScreen();
            printBanner();
            System.out.println("  URZĄDZENIA");
            System.out.println(THIN);
            printDeviceRow(1, "Kamera",             manager.getCamera());
            printDeviceRow(2, "Ogrzewanie",         manager.getHeating());
            printDeviceRow(3, "Światło",            manager.getLight());
            printDeviceRow(4, "Jonizacja powietrza",manager.getIonization());
            printDeviceRow(5, "Głośniki",           manager.getSpeakers());
            printDeviceRow(6, "Pralka",             manager.getWashing());
            System.out.println(THIN);
            System.out.println("  7. Wróć do menu głównego");
            System.out.println(LINE);
            System.out.print("  Wybierz urządzenie: ");

            switch (readLine()) {
                case "1" -> manageCamera();
                case "2" -> manageHeating();
                case "3" -> manageLight();
                case "4" -> manageGeneric("Jonizacja powietrza", manager.getIonization());
                case "5" -> manageGeneric("Głośniki", manager.getSpeakers());
                case "6" -> manageWashing();
                case "7" -> back = true;
                default  -> showError("Nieznana opcja.");
            }
        }
    }

    private void manageCamera() {
        Camera cam = manager.getCamera();
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.println("  KAMERA  |  Status: " + statusLabel(cam.checkStatus()));
            System.out.println(THIN);
            System.out.println("  1. Włącz / Wyłącz");
            System.out.println("  2. Wróć");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String choice = readLine();
            if (choice.equals("1")) { toggleDevice(cam); }
            else if (choice.equals("2")) { break; }
            else { showError("Nieznana opcja."); }
        }
    }

    private void manageHeating() {
        Heating h = manager.getHeating();
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.printf("  OGRZEWANIE  |  Status: %s  |  Temp: %.1f°C%n",
                    statusLabel(h.checkStatus()), h.getTemperature());
            System.out.println(THIN);
            System.out.println("  1. Włącz / Wyłącz");
            System.out.println("  2. Ustaw temperaturę");
            System.out.println("  3. Wróć");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String choice = readLine();
            if (choice.equals("1")) {
                toggleDevice(h);
            } else if (choice.equals("2")) {
                System.out.print("  Podaj temperaturę (°C): ");
                try {
                    float t = Float.parseFloat(readLine());
                    h.setTemperature(t);
                    System.out.println("  [OK] Temperatura ustawiona na " + t + "°C.");
                    pause(800);
                } catch (NumberFormatException e) {
                    showError("Podaj liczbę.");
                }
            } else if (choice.equals("3")) {
                break;
            } else {
                showError("Nieznana opcja.");
            }
        }
    }

    private void manageLight() {
        Light l = manager.getLight();
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.printf("  ŚWIATŁO  |  Status: %s  |  Jasność: %d%%%n",
                    statusLabel(l.checkStatus()), l.getBrightnessLevel());
            System.out.println(THIN);
            System.out.println("  1. Włącz / Wyłącz");
            System.out.println("  2. Ustaw poziom jasności (0-100)");
            System.out.println("  3. Wróć");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String choice = readLine();
            if (choice.equals("1")) {
                toggleDevice(l);
            } else if (choice.equals("2")) {
                System.out.print("  Poziom jasności (0-100): ");
                try {
                    int lvl = Integer.parseInt(readLine());
                    if (lvl < 0 || lvl > 100) { showError("Podaj wartość 0-100."); }
                    else { l.changeLevel(lvl); System.out.println("  [OK] Jasność: " + lvl + "%"); pause(800); }
                } catch (NumberFormatException e) {
                    showError("Podaj liczbę całkowitą.");
                }
            } else if (choice.equals("3")) {
                break;
            } else {
                showError("Nieznana opcja.");
            }
        }
    }

    private void manageWashing() {
        Washing w = manager.getWashing();
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.println("  PRALKA  |  Status: " + statusLabel(w.checkStatus()));
            System.out.println(THIN);
            System.out.println("  1. Włącz / Wyłącz");
            System.out.println("  2. Wróć");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String choice = readLine();
            if (choice.equals("1")) { toggleDevice(w); }
            else if (choice.equals("2")) { break; }
            else { showError("Nieznana opcja."); }
        }
    }

    private void manageGeneric(String name,
            org.example.interfaces.ObslugaFunkcjonalnosciDomu device) {
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.println("  " + name.toUpperCase() + "  |  Status: " + statusLabel(device.checkStatus()));
            System.out.println(THIN);
            System.out.println("  1. Włącz / Wyłącz");
            System.out.println("  2. Wróć");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String choice = readLine();
            if (choice.equals("1")) { toggleDevice(device); }
            else if (choice.equals("2")) { break; }
            else { showError("Nieznana opcja."); }
        }
    }

    // ── Sensory ───────────────────────────────────────────────────────────────

    private void sensorsMenu() {
        clearScreen();
        printBanner();
        System.out.println("  ODCZYT SENSORÓW");
        System.out.println(THIN);
        SolarPanel sp = manager.getSolarPanel();
        System.out.printf("  [SOLAR] Panel słoneczny     :  %.2f kWh%n", sp.readValue());
        System.out.printf("  [POWIETRZE] Jonizacja powietrza :  %.2f (jednostki)%n",
                manager.getIonization().readValue());
        System.out.println(THIN);
        System.out.println("  1. Ustaw energię panelu słonecznego");
        System.out.println("  2. Wróć");
        System.out.println(LINE);
        System.out.print("  Wybierz: ");
        String choice = readLine();
        if (choice.equals("1")) {
            System.out.print("  Podaj wartość energii (kWh): ");
            try {
                float val = Float.parseFloat(readLine());
                sp.setEnergy(val);
                System.out.println("  [OK] Energia panelu ustawiona: " + val + " kWh");
                pause(800);
            } catch (NumberFormatException e) {
                showError("Podaj liczbę.");
            }
        }
    }

    // ── Symulacja zdarzeń ─────────────────────────────────────────────────────

    private void simulateMenu() {
        boolean back = false;
        while (!back) {
            clearScreen();
            printBanner();
            System.out.println("  SYMULACJA ZDARZEŃ");
            System.out.println(THIN);
            System.out.println("  1. Ruch wykryty przez kamerę");
            System.out.println("  2. Niska temperatura (ogrzewanie)");
            System.out.println("  3. Pranie zakończone");
            System.out.println("  4. Wróć");
            System.out.println(LINE);
            System.out.print("  Wybierz zdarzenie: ");

            switch (readLine()) {
                case "1" -> {
                    System.out.println("\n  >>> Symulacja: wykryto ruch <<<");
                    System.out.println(THIN);
                    manager.getCamera().motionDetection();
                    System.out.println(THIN);
                    System.out.println("  [Naciśnij Enter aby kontynuować]");
                    scanner.nextLine();
                }
                case "2" -> {
                    System.out.println("\n  >>> Symulacja: niska temperatura <<<");
                    System.out.println(THIN);
                    // Wymuszenie niskiej temperatury do symulacji
                    Heating h = manager.getHeating();
                    float orig = h.getTemperature();
                    h.setTemperature(10.0f);
                    h.temperatureDetection();
                    h.setTemperature(orig);
                    System.out.println(THIN);
                    System.out.println("  [Naciśnij Enter aby kontynuować]");
                    scanner.nextLine();
                }
                case "3" -> {
                    System.out.println("\n  >>> Symulacja: pranie zakończone <<<");
                    System.out.println(THIN);
                    Washing w = manager.getWashing();
                    w.setFinished(true);
                    w.isWashingFinished();
                    w.setFinished(false);   // reset po symulacji
                    System.out.println(THIN);
                    System.out.println("  [Naciśnij Enter aby kontynuować]");
                    scanner.nextLine();
                }
                case "4" -> back = true;
                default  -> showError("Nieznana opcja.");
            }
        }
    }

    // ── Log AI ────────────────────────────────────────────────────────────────

    private void aiLogMenu() {
        clearScreen();
        printBanner();
        System.out.println("  LOG AI ASYSTENTA");
        System.out.println(THIN);

        List<String> log = manager.getAssistantAi().getEventLog();
        if (log.isEmpty()) {
            System.out.println("  (brak zdarzeń — najpierw zasymuluj coś)");
        } else {
            log.forEach(entry -> System.out.println("  " + entry));
        }

        System.out.println(LINE);
        System.out.println("  [Naciśnij Enter aby wrócić]");
        scanner.nextLine();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void toggleDevice(org.example.interfaces.ObslugaFunkcjonalnosciDomu device) {
        if (device.checkStatus()) {
            device.turnOff();
            System.out.println("  [OK] Urządzenie wyłączone.");
        } else {
            device.turnOn();
            System.out.println("  [OK] Urządzenie włączone.");
        }
        pause(700);
    }

    private void printDeviceRow(int idx, String name,
            org.example.interfaces.ObslugaFunkcjonalnosciDomu device) {
        System.out.printf("  %d. %-22s [%s]%n", idx, name, statusLabel(device.checkStatus()));
    }

    private String statusLabel(boolean active) {
        return active ? " ON " : "OFF";
    }

    private void showError(String msg) {
        System.out.println("\n  [!!] " + msg + " Naciśnij Enter.");
        scanner.nextLine();
    }

    private String readLine() {
        return scanner.nextLine().trim();
    }

    private void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printBanner() {
        System.out.println(LINE);
        System.out.println("       >>> SMART HOME SYSTEM  v1.0");
        System.out.println(LINE);
    }
}
