package org.example.models;


import org.example.Observed;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;


public class Camera extends Observed implements ObslugaFunkcjonalnosciDomu {
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
    public void motionDetection() {
        System.out.println("*Kamera klika i nagrywa*");
        notifyObservers(Camera.class.toString(), "Ruch");
    }

    @Override
    public String toString() {
        return "Camera{" +
                "isActive=" + isActive +
                '}';
    }
}
