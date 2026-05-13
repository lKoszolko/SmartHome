package org.example.models;


import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

public class Washing implements ObslugaFunkcjonalnosciDomu {
    boolean isActive = false;
    boolean isFinished;

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
        return "Washing{" +
                "isActive=" + isActive +
                ", isFinished=" + isFinished +
                '}';
    }
}
