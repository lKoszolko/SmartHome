package org.example.managers;

import org.example.Observed;
import org.example.interfaces.IObserver;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.interfaces.Sensors;

import java.util.ArrayList;
import java.util.List;

public class AiAssistant implements IObserver {
    private List<ObslugaFunkcjonalnosciDomu> devices = new ArrayList<>();
    private List<Sensors> sensors = new ArrayList<>();
    private String currentMode;

    public AiAssistant(List<ObslugaFunkcjonalnosciDomu> devices){
        this.devices = devices;}
    public AiAssistant(){}

    public List<ObslugaFunkcjonalnosciDomu> getDevices(){
        return devices;
    }

    @Override
    public void reagujNaZdarzenie(String source, String eventType) {
        System.out.println("AI INFO: Zarejestrowano zdarzenie z [" + source + "]. Typ: " + eventType);
        if (source.equals("Camera") && eventType.equals("Movement")) {
            System.out.println("AI AKCJA: Włączam światło i wysyłam powiadomienie do mieszkańca!");
        }
    }

    public void addDevice(ObslugaFunkcjonalnosciDomu device){
        devices.add(device);
    }

    public void turnOffDevice(ObslugaFunkcjonalnosciDomu o){
        o.turnOff();
    }

    public void turnOnDevice(ObslugaFunkcjonalnosciDomu o){
        o.turnOn();
    }

    public void printActiveDevices(){
        devices.stream().filter(ObslugaFunkcjonalnosciDomu::checkStatus).forEach(System.out::println);
    }
}
