package org.example.models;


import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

public class Light implements ObslugaFunkcjonalnosciDomu {
    boolean isActive = false;
    int brightnessLevel;

    @Override
    public String toString() {
        return "Light{" +
                "brightnessLevel=" + brightnessLevel +
                ", isActive=" + isActive +
                '}';
    }

    public void changeLevel(int level){
        if (isActive){
            this.brightnessLevel = level;
        } else {
            System.out.println("Nie mozesz zmienic poziomu swiatla dopoki go nie wlaczysz");
        }
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
}
