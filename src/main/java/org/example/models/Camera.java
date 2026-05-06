package org.example.models;


import org.example.interfaces.Observer;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

import java.util.ArrayList;
import java.util.List;

public class Camera implements ObslugaFunkcjonalnosciDomu {

    List<Observer> observer = new ArrayList<>();
    boolean isActive;

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

    public void addobservator(Observer observer){

    }

    @Override
    public String toString() {
        return "Camera{" +
                "isActive=" + isActive +
                '}';
    }
}
