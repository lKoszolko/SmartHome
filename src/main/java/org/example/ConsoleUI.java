package org.example;

import org.example.managers.AiAssistant;
import org.example.managers.AiAssistant.DeviceName;
import org.example.managers.SmartHomeManager;
import org.example.storage.LogEntry;

import java.util.List;
import java.util.Scanner;


public class ConsoleUI {

    private static final String LINE =
            "==================================================";
    private static final String THIN =
            "--------------------------------------------------";

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
            System.out.println("\n  [!!] Bledny PIN. Dostep zablokowany.\n");
            return;
        }

        manager.initialize();
        System.out.println("\n  [OK] Autoryzacja pomyslna. System uruchomiony.\n");
        pause(1000);

        mainMenu();
        System.out.println("\n  Do widzenia!\n");
    }

    // ── Logowanie ─────────────────────────────────────────────────────────────

    private boolean loginScreen() {
        clearScreen();
        printBanner();
        System.out.println("  Podaj PIN aby sie zalogowac:");
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
            System.out.println("  MENU GLOWNE");
            System.out.println(THIN);
            System.out.println("  1. Zarzadzaj urzadzeniami");
            System.out.println("  2. Odczyt sensorow");
            System.out.println("  3. Symuluj zdarzenie");
            System.out.println("  4. Log AI (sesja)");
            System.out.println("  5. Historia logów (JSON)");
            System.out.println("  6. Tryb awaryjny");
            System.out.println("  7. Wyjscie");
            System.out.println(LINE);
            System.out.print("  Wybierz opcje: ");

            switch (readLine()) {
                case "1" -> devicesMenu();
                case "2" -> sensorsMenu();
                case "3" -> simulateMenu();
                case "4" -> sessionLogMenu();
                case "5" -> historyLogMenu();
                case "6" -> emergencyMode();
                case "7" -> running = false;
                default  -> showError("Nieznana opcja.");
            }
        }
    }

    // ── Urządzenia ────────────────────────────────────────────────────────────

    private void devicesMenu() {
        AiAssistant ai = manager.getFacade();
        boolean back = false;
        while (!back) {
            clearScreen();
            printBanner();
            System.out.println("  URZADZENIA");
            System.out.println(THIN);
            printRow(1, "Kamera",             ai.isActive(DeviceName.KAMERA));
            printRow(2, "Ogrzewanie",         ai.isActive(DeviceName.OGRZEWANIE));
            printRow(3, "Swiatlo",            ai.isActive(DeviceName.SWIATLO));
            printRow(4, "Jonizacja powietrza",ai.isActive(DeviceName.JONIZACJA));
            printRow(5, "Glosniki",           ai.isActive(DeviceName.GLOSNIKI));
            printRow(6, "Pralka",             ai.isActive(DeviceName.PRALKA));
            System.out.println(THIN);
            System.out.println("  7. Wróc do menu glownego");
            System.out.println(LINE);
            System.out.print("  Wybierz urzadzenie: ");

            switch (readLine()) {
                case "1" -> manageSimple("Kamera",             DeviceName.KAMERA);
                case "2" -> manageHeating();
                case "3" -> manageLight();
                case "4" -> manageSimple("Jonizacja powietrza",DeviceName.JONIZACJA);
                case "5" -> manageSimple("Glosniki",           DeviceName.GLOSNIKI);
                case "6" -> manageSimple("Pralka",             DeviceName.PRALKA);
                case "7" -> back = true;
                default  -> showError("Nieznana opcja.");
            }
        }
    }

    private void manageSimple(String name, DeviceName device) {
        AiAssistant ai = manager.getFacade();
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.println("  " + name.toUpperCase() + "  |  Status: " + status(ai.isActive(device)));
            System.out.println(THIN);
            System.out.println("  1. Wlacz / Wylacz");
            System.out.println("  2. Wróc");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String c = readLine();
            if (c.equals("1"))      { toggle(ai, device); }
            else if (c.equals("2")) { break; }
            else                    { showError("Nieznana opcja."); }
        }
    }

    private void manageHeating() {
        AiAssistant ai = manager.getFacade();
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.printf("  OGRZEWANIE  |  Status: %s  |  Temp: %.1f stopni%n",
                    status(ai.isActive(DeviceName.OGRZEWANIE)), ai.getTemperature());
            System.out.println(THIN);
            System.out.println("  1. Wlacz / Wylacz");
            System.out.println("  2. Ustaw temperature");
            System.out.println("  3. Wróc");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String c = readLine();
            if (c.equals("1")) {
                toggle(ai, DeviceName.OGRZEWANIE);
            } else if (c.equals("2")) {
                System.out.print("  Podaj temperature (stopnie): ");
                try {
                    float t = Float.parseFloat(readLine());
                    ai.setTemperature(t);
                    System.out.println("  [OK] Temperatura ustawiona: " + t);
                    pause(800);
                } catch (NumberFormatException e) { showError("Podaj liczbe."); }
            } else if (c.equals("3")) {
                break;
            } else {
                showError("Nieznana opcja.");
            }
        }
    }

    private void manageLight() {
        AiAssistant ai = manager.getFacade();
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.printf("  SWIATLO  |  Status: %s  |  Jasnosc: %d%%%n",
                    status(ai.isActive(DeviceName.SWIATLO)), ai.getBrightness());
            System.out.println(THIN);
            System.out.println("  1. Wlacz / Wylacz");
            System.out.println("  2. Ustaw poziom jasnosci (0-100)");
            System.out.println("  3. Wróc");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String c = readLine();
            if (c.equals("1")) {
                toggle(ai, DeviceName.SWIATLO);
            } else if (c.equals("2")) {
                System.out.print("  Poziom jasnosci (0-100): ");
                try {
                    int lvl = Integer.parseInt(readLine());
                    if (lvl < 0 || lvl > 100) { showError("Podaj wartosc 0-100."); }
                    else { ai.setBrightness(lvl); System.out.println("  [OK] Jasnosc: " + lvl + "%"); pause(800); }
                } catch (NumberFormatException e) { showError("Podaj liczbe calkowita."); }
            } else if (c.equals("3")) {
                break;
            } else {
                showError("Nieznana opcja.");
            }
        }
    }

    // ── Sensory ───────────────────────────────────────────────────────────────

    private void sensorsMenu() {
        AiAssistant ai = manager.getFacade();
        clearScreen();
        printBanner();
        System.out.println("  ODCZYT SENSOROW");
        System.out.println(THIN);
        System.out.printf("  [SOLAR] Panel sloneczny     :  %.2f kWh%n", ai.readSolarEnergy());
        System.out.printf("  [AIR]   Jonizacja powietrza :  %.2f (jednostki)%n", ai.readIonization());
        System.out.println(THIN);
        System.out.println("  1. Ustaw energie panelu slonecznego");
        System.out.println("  2. Wróc");
        System.out.println(LINE);
        System.out.print("  Wybierz: ");
        if (readLine().equals("1")) {
            System.out.print("  Podaj wartosc energii (kWh): ");
            try {
                float val = Float.parseFloat(readLine());
                ai.setSolarEnergy(val);
                System.out.println("  [OK] Energia panelu ustawiona: " + val + " kWh");
                pause(800);
            } catch (NumberFormatException e) { showError("Podaj liczbe."); }
        }
    }

    // ── Symulacja ─────────────────────────────────────────────────────────────

    private void simulateMenu() {
        AiAssistant ai = manager.getFacade();
        boolean back = false;
        while (!back) {
            clearScreen();
            printBanner();
            System.out.println("  SYMULACJA ZDARZEN");
            System.out.println(THIN);
            System.out.println("  1. Ruch wykryty przez kamere");
            System.out.println("  2. Niska temperatura (ogrzewanie)");
            System.out.println("  3. Pranie zakonczone");
            System.out.println("  4. Wróc");
            System.out.println(LINE);
            System.out.print("  Wybierz zdarzenie: ");

            switch (readLine()) {
                case "1" -> {
                    System.out.println("\n  >>> Symulacja: wykryto ruch <<<");
                    System.out.println(THIN);
                    ai.simulateCameraMotion();
                    System.out.println(THIN);
                    System.out.println("  [Nacisnij Enter aby kontynuowac]");
                    scanner.nextLine();
                }
                case "2" -> {
                    System.out.println("\n  >>> Symulacja: niska temperatura <<<");
                    System.out.println(THIN);
                    ai.simulateLowTemperature();
                    System.out.println(THIN);
                    System.out.println("  [Nacisnij Enter aby kontynuowac]");
                    scanner.nextLine();
                }
                case "3" -> {
                    System.out.println("\n  >>> Symulacja: pranie zakonczone <<<");
                    System.out.println(THIN);
                    ai.simulateWashingFinished();
                    System.out.println(THIN);
                    System.out.println("  [Nacisnij Enter aby kontynuowac]");
                    scanner.nextLine();
                }
                case "4" -> back = true;
                default  -> showError("Nieznana opcja.");
            }
        }
    }

    // ── Log sesji ─────────────────────────────────────────────────────────────

    private void sessionLogMenu() {
        clearScreen();
        printBanner();
        System.out.println("  LOG AI — BIEZACA SESJA");
        System.out.println(THIN);
        List<String> log = manager.getFacade().getSessionLog();
        if (log.isEmpty()) {
            System.out.println("  (brak zdarzen — najpierw zasymuluj cos)");
        } else {
            log.forEach(e -> System.out.println("  " + e));
        }
        System.out.println(LINE);
        System.out.println("  [Nacisnij Enter aby wrócic]");
        scanner.nextLine();
    }

    // ── Historia z JSON ───────────────────────────────────────────────────────

    private void historyLogMenu() {
        boolean back = false;
        while (!back) {
            clearScreen();
            printBanner();
            System.out.println("  HISTORIA LOGOW (plik JSON)");
            System.out.println(THIN);
            List<LogEntry> history = manager.getFacade().getFullLog();
            if (history.isEmpty()) {
                System.out.println("  (brak zapisanych zdarzen)");
            } else {
                // Pokazuj ostatnie 20, żeby nie zalewać ekranu
                int start = Math.max(0, history.size() - 20);
                if (start > 0)
                    System.out.println("  ... (+" + start + " wczesniejszych wpisów) ...");
                for (int i = start; i < history.size(); i++) {
                    System.out.println("  " + history.get(i));
                }
            }
            System.out.println(THIN);
            System.out.println("  1. Wyczysc historie logow");
            System.out.println("  2. Wróc");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String c = readLine();
            if (c.equals("1")) {
                manager.getFacade().clearLog();
                System.out.println("  [OK] Historia wyczyszczona.");
                pause(800);
            } else if (c.equals("2")) {
                back = true;
            } else {
                showError("Nieznana opcja.");
            }
        }
    }

    // ── Tryb awaryjny ─────────────────────────────────────────────────────────

    private void emergencyMode() {
        clearScreen();
        printBanner();
        System.out.println("  TRYB AWARYJNY");
        System.out.println(THIN);
        System.out.println("  Wszystkie urzadzenia zostana wyłączone!");
        System.out.print("  Potwierdz (T/N): ");
        String confirm = readLine().toUpperCase();
        if (confirm.equals("T")) {
            manager.activateEmergency();
            System.out.println("  [OK] System wyłączony awaryjnie.");
            pause(1200);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void toggle(AiAssistant ai, DeviceName d) {
        if (ai.isActive(d)) { ai.turnOff(d); System.out.println("  [OK] Urzadzenie wylaczone."); }
        else                { ai.turnOn(d);  System.out.println("  [OK] Urzadzenie wlaczone.");  }
        pause(700);
    }

    private void printRow(int idx, String name, boolean active) {
        System.out.printf("  %d. %-22s [%s]%n", idx, name, status(active));
    }

    private String status(boolean active) { return active ? " ON " : "OFF"; }

    private void showError(String msg) {
        System.out.println("\n  [!!] " + msg + " Nacisnij Enter.");
        scanner.nextLine();
    }

    private String readLine() { return scanner.nextLine().trim(); }

    private void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printBanner() {
        System.out.println(LINE);
        System.out.println("       >>> SMART HOME SYSTEM  v2.0");
        System.out.println(LINE);
    }
}
