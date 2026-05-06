package org.example.models;

import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.interfaces.Sensors;

public class AirIonization implements ObslugaFunkcjonalnosciDomu, Sensors {
    private boolean isActive;
    private String airCondition;


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
    public float readValue() {
        return 0;
    }

    @Override
    public String toString() {
        return "AirIonization{" +
                "isActive=" + isActive +
                ", airCondition='" + airCondition + '\'' +
                '}';
    }
}
