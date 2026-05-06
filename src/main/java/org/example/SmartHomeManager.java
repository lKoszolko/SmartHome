package org.example;

import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

import java.util.List;

public class SmartHomeManager {
    Inhabitant inhabitant;

    public void initialize(List<ObslugaFunkcjonalnosciDomu> o){
        o.forEach(AiAssistant::addDevice);
    }

    public void activateEmergency(){

    }

    public boolean authorize(int pin){
        return inhabitant.getPin() == pin;
    }



}
