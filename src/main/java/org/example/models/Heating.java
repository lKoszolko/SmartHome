package org.example.models;

import org.example.interfaces.Observer;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

import java.util.ArrayList;
import java.util.List;

public class Heating implements ObslugaFunkcjonalnosciDomu {
    private boolean isActive;
    private float temperature;
    List<Observer> observers = new ArrayList<>();

    public Heating(float temperature){
        this.temperature = temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    @Override
    public void turnOn() {

    }

    @Override
    public void turnOff() {

    }

    @Override
    public boolean checkStatus() {
        return false;
    }
}
