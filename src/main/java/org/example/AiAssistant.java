package org.example;

import org.example.interfaces.Observer;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.interfaces.Sensors;

import java.util.ArrayList;
import java.util.List;

public class AiAssistant implements Observer {
    private static List<ObslugaFunkcjonalnosciDomu> devices = new ArrayList<>();
    private List<Sensors> sensors = new ArrayList<>();
    private String currentMode;
    private SmartHomeManager manager = new SmartHomeManager();

    public AiAssistant(List<ObslugaFunkcjonalnosciDomu> devices){
        AiAssistant.devices = devices;
    }

    @Override
    public void reagujNaZdarzenie(String source, String eventType) {

    }

    public static void addDevice(ObslugaFunkcjonalnosciDomu device){
        devices.add(device);
    }

    public void turnOffDevice(ObslugaFunkcjonalnosciDomu o){
        o.turnOff();
    }

    public void printActiveDevices(List<ObslugaFunkcjonalnosciDomu> list){
        list.stream().filter(ObslugaFunkcjonalnosciDomu::checkStatus).forEach(System.out::println);
    }



}
