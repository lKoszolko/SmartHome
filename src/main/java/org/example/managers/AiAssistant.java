package org.example.managers;

import org.example.interfaces.Observer;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.interfaces.Sensors;

import java.util.ArrayList;
import java.util.List;

public class AiAssistant implements Observer {
    private List<ObslugaFunkcjonalnosciDomu> devices = new ArrayList<>();
    private List<Sensors> sensors = new ArrayList<>();
    private String currentMode;
//    private SmartHomeManager manager = new SmartHomeManager();

    public AiAssistant(List<ObslugaFunkcjonalnosciDomu> devices){
        this.devices = devices;
    }
    public AiAssistant(){

    }
    public List<ObslugaFunkcjonalnosciDomu> getDevices(){
        return devices;
    }

    @Override
    public void reagujNaZdarzenie(String source, String eventType) {
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

    public void printActiveDevices(List<ObslugaFunkcjonalnosciDomu> list){
        list.stream().filter(ObslugaFunkcjonalnosciDomu::checkStatus).forEach(System.out::println);
    }
}
