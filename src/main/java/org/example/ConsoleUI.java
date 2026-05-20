package org.example;

import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.managers.AiAssistant;
import org.example.managers.AiAssistantImpl;
import org.example.managers.SmartHomeManager;
import org.example.models.*;
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

    // Referencje do fasady i urzadzen — ustawiane po inicjalizacji systemu
    private AiAssistant     ai;
    private Camera          camera;
    private Heating         heating;
    private Light           light;
    private AirIonization   ionization;
    private SolarPanel      solarPanel;
    private Speakers        speakers;
    private Washing         washing;

    public ConsoleUI(SmartHomeManager manager) {
        this.manager = manager;
        this.scanner = new Scanner(System.in);
    }

    //start
    public void start() {
        clearScreen();
        printBanner();

        if (!loginScreen()) {
            System.out.println("\n  [!!] Bledny PIN. Dostep zablokowany.\n");
            return;
        }

        manager.initialize();

        // Pobranie referencji do fasady i urzadzen po inicjalizacji
        AiAssistantImpl impl = manager.getFacade();
        ai         = impl;
        camera     = impl.getCamera();
        heating    = impl.getHeating();
        light      = impl.getLight();
        ionization = impl.getIonization();
        solarPanel = impl.getSolarPanel();
        speakers   = impl.getSpeakers();
        washing    = impl.getWashing();

        System.out.println("\n  [OK] Autoryzacja pomyslna. System uruchomiony.\n");
        pause(1000);

        mainMenu();
        System.out.println("\n  Do widzenia!\n");
    }

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


    private void devicesMenu() {
        boolean back = false;
        while (!back) {
            clearScreen();
            printBanner();
            System.out.println("  URZADZENIA");
            System.out.println(THIN);
            printRow(1, "Kamera",             ai.isActive(camera));
            printRow(2, "Ogrzewanie",         ai.isActive(heating));
            printRow(3, "Swiatlo",            ai.isActive(light));
            printRow(4, "Jonizacja powietrza",ai.isActive(ionization));
            printRow(5, "Glosniki",           ai.isActive(speakers));
            printRow(6, "Pralka",             ai.isActive(washing));
            System.out.println(THIN);
            System.out.println("  7. Wróc do menu glownego");
            System.out.println(LINE);
            System.out.print("  Wybierz urzadzenie: ");

            switch (readLine()) {
                case "1" -> manageSimple("Kamera",             camera);
                case "2" -> manageHeating();
                case "3" -> manageLight();
                case "4" -> manageSimple("Jonizacja powietrza",ionization);
                case "5" -> manageSimple("Glosniki",           speakers);
                case "6" -> manageSimple("Pralka",             washing);
                case "7" -> back = true;
                default  -> showError("Nieznana opcja.");
            }
        }
    }

    private void manageSimple(String name, ObslugaFunkcjonalnosciDomu device) {
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
            if (c.equals("1"))      { toggle(device); }
            else if (c.equals("2")) { break; }
            else                    { showError("Nieznana opcja."); }
        }
    }

    private void manageHeating() {
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.printf("  OGRZEWANIE  |  Status: %s  |  Temp: %.1f stopni%n",
                    status(ai.isActive(heating)), heating.getTemperature());
            System.out.println(THIN);
            System.out.println("  1. Wlacz / Wylacz");
            System.out.println("  2. Ustaw temperature");
            System.out.println("  3. Wróc");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String c = readLine();
            if (c.equals("1")) {
                toggle(heating);
            } else if (c.equals("2")) {
                System.out.print("  Podaj temperature (stopnie): ");
                try {
                    float t = Float.parseFloat(readLine());
                    heating.setTemperature(t);
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
        while (true) {
            clearScreen();
            System.out.println(LINE);
            System.out.printf("  SWIATLO  |  Status: %s  |  Jasnosc: %d%%%n",
                    status(ai.isActive(light)), light.getBrightnessLevel());
            System.out.println(THIN);
            System.out.println("  1. Wlacz / Wylacz");
            System.out.println("  2. Ustaw poziom jasnosci (0-100)");
            System.out.println("  3. Wróc");
            System.out.println(LINE);
            System.out.print("  Wybierz: ");
            String c = readLine();
            if (c.equals("1")) {
                toggle(light);
            } else if (c.equals("2")) {
                System.out.print("  Poziom jasnosci (0-100): ");
                try {
                    int lvl = Integer.parseInt(readLine());
                    if (lvl < 0 || lvl > 100) { showError("Podaj wartosc 0-100."); }
                    else { light.changeLevel(lvl); System.out.println("  [OK] Jasnosc: " + lvl + "%"); pause(800); }
                } catch (NumberFormatException e) { showError("Podaj liczbe calkowita."); }
            } else if (c.equals("3")) {
                break;
            } else {
                showError("Nieznana opcja.");
            }
        }
    }

    private void sensorsMenu() {
        clearScreen();
        printBanner();
        System.out.println("  ODCZYT SENSOROW");
        System.out.println(THIN);
        System.out.printf("  [SOLAR] Panel sloneczny     :  %.2f kWh%n",        solarPanel.readValue());
        System.out.printf("  [AIR]   Jonizacja powietrza :  %.2f (jednostki)%n", ionization.readValue());
        System.out.println(THIN);
        System.out.println("  1. Ustaw energie panelu slonecznego");
        System.out.println("  2. Wróc");
        System.out.println(LINE);
        System.out.print("  Wybierz: ");
        if (readLine().equals("1")) {
            System.out.print("  Podaj wartosc energii (kWh): ");
            try {
                float val = Float.parseFloat(readLine());
                solarPanel.setEnergy(val);
                System.out.println("  [OK] Energia panelu ustawiona: " + val + " kWh");
                pause(800);
            } catch (NumberFormatException e) { showError("Podaj liczbe."); }
        }
    }

    //!!!SYMULACJE!!!
    private void simulateMenu() {
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

    //logi
    private void sessionLogMenu() {
        clearScreen();
        printBanner();
        System.out.println("  LOG AI — BIEZACA SESJA");
        System.out.println(THIN);
        List<String> log = ai.getSessionLog();
        if (log.isEmpty()) {
            System.out.println("  (brak zdarzen — najpierw zasymuluj cos)");
        } else {
            log.forEach(e -> System.out.println("  " + e));
        }
        System.out.println(LINE);
        System.out.println("  [Nacisnij Enter aby wrócic]");
        scanner.nextLine();
    }

    //Json
    private void historyLogMenu() {
        boolean back = false;
        while (!back) {
            clearScreen();
            printBanner();
            System.out.println("  HISTORIA LOGOW (plik JSON)");
            System.out.println(THIN);
            List<LogEntry> history = ai.getFullLog();
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
                ai.clearLog();
                System.out.println("  [OK] Historia wyczyszczona.");
                pause(800);
            } else if (c.equals("2")) {
                back = true;
            } else {
                showError("Nieznana opcja.");
            }
        }
    }

    //trybawaryjny(Wszystkof)
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

    private void toggle(ObslugaFunkcjonalnosciDomu device) {
        if (ai.isActive(device)) { ai.turnOff(device); System.out.println("  [OK] Urzadzenie wylaczone."); }
        else                     { ai.turnOn(device);  System.out.println("  [OK] Urzadzenie wlaczone.");  }
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
