package org.example.models;

import org.example.Observed;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

public class Heating extends Observed implements ObslugaFunkcjonalnosciDomu {
    private boolean isActive;
    private float temperature = 20.0f;

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getTemperature() {
        return temperature;
    }

    @Override public void turnOn() { if (!isActive) isActive = true;  }
    @Override public void turnOff() {if (isActive)  isActive = false; }
    @Override public boolean checkStatus() {return isActive; }

    @Override
    public String toString() {
        return "Heating{isActive=" + isActive + ", temperature=" + temperature + "}";
    }

    public void temperatureDetection() {
        if (temperature < 15) {
            notifyObservers(Heating.class.toString(), "Niska temperatura");
        }
    }
}
