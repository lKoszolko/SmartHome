package org.example.managers;

import org.example.models.Inhabitant;


public class SmartHomeManager {

    private static final String LOG_FILE = "smarthome_logs.json";

    private final Inhabitant inhabitant;
    private AiAssistant facade;

    public SmartHomeManager(Inhabitant inhabitant) {
        this.inhabitant = inhabitant;
    }

    public void initialize() {
        facade = new AiAssistant(LOG_FILE);
    }

    public boolean authorize(int pin) {
        return inhabitant.getPin() == pin;
    }

    //Fasada
    public void activateEmergency() {
        if (facade == null) return;
        for (AiAssistant.DeviceName d : AiAssistant.DeviceName.values()) {
            facade.turnOff(d);
        }
        System.out.println("[!!] Tryb awaryjny: wszystkie urządzenia wyłączone.");
    }

    //Zwraca fasadę
    public AiAssistant getFacade() { return facade; }
}
