package org.example.models;

import org.example.interfaces.Observer;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

import java.util.ArrayList;
import java.util.List;

public class Heating implements ObslugaFunkcjonalnosciDomu {
    private boolean isActive;
    private float temperature;
    List<Observer> observers = new ArrayList<>();

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    @Override
    public void turnOn() {
        if (!isActive) isActive = true;
    }

    @Override
    public void turnOff() {
        if (isActive) isActive = false;
    }

    @Override
    public boolean checkStatus() {
        return isActive;
    }

    @Override
    public String toString() {
        return "Heating{" +
                "isActive=" + isActive +
                ", temperature=" + temperature +
                '}';
    }
}
