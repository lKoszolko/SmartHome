package org.example.models;


import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

public class Speakers implements ObslugaFunkcjonalnosciDomu {
    boolean isActive;

    @Override
    public void turnOn() {if (!isActive) isActive = true;}

    @Override
    public void turnOff() {if (isActive) isActive = false;}

    @Override
    public boolean checkStatus(){return isActive;}

    @Override
    public String toString() {
        return "Speakers{" +
                "isActive=" + isActive +
                '}';
    }
}
