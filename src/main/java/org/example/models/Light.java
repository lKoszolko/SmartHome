package org.example.models;

import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

public class Light implements ObslugaFunkcjonalnosciDomu {
    boolean isActive = false;
    int brightnessLevel = 0;

    public int getBrightnessLevel() { return brightnessLevel; }

    public void changeLevel(int level) {
        if (isActive) {
            this.brightnessLevel = level;
        } else {
            System.out.println("Nie możesz zmienić poziomu światła dopóki go nie włączysz.");
        }
    }

    @Override public void turnOn()  { if (!isActive) isActive = true;  }
    @Override public void turnOff() { if (isActive)  isActive = false; }
    @Override public boolean checkStatus() { return isActive; }

    @Override
    public String toString() {
        return "Light{brightnessLevel=" + brightnessLevel + ", isActive=" + isActive + "}";
    }
}
