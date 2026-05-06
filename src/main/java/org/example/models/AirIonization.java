package org.example.models;

import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.interfaces.Sensors;

public class AirIonization implements ObslugaFunkcjonalnosciDomu, Sensors {
    private boolean isActive;
    private String airCondition;
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

    @Override
    public float readValue() {
        return 0;
    }
}
