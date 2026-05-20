package org.example.managers;

import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.storage.LogEntry;

import java.util.List;


public interface AiAssistant {

    //Wlaczanie urzadzenia
    void turnOn(ObslugaFunkcjonalnosciDomu device);
    void turnOff(ObslugaFunkcjonalnosciDomu device);
    boolean isActive(ObslugaFunkcjonalnosciDomu device);

    //!!!SYMULACJE!!!
    void simulateCameraMotion();
    void simulateLowTemperature();
    void simulateWashingFinished();

    //logi
    List<String> getSessionLog();
    List<LogEntry> getFullLog();
    void clearLog();
}
