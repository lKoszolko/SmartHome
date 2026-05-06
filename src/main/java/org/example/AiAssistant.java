package org.example;

import org.example.interfaces.Observer;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.interfaces.Sensors;

import java.util.ArrayList;
import java.util.List;

public class AiAssistant implements Observer {
    List<ObslugaFunkcjonalnosciDomu> devices = new ArrayList<>();
    List<Sensors> sensors = new ArrayList<>();
    String currentMode;

    @Override
    public void reagujNaZdarzenie(String source, String eventType) {

    }

    public void addDevice(ObslugaFunkcjonalnosciDomu device){
        devices.add(device);
    }

    public void turnOnDevice(String name){

    }

}
