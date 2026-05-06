package org.example.models;


import org.example.interfaces.Sensors;

public class SolarPanel implements Sensors {

    private double energy;

    public SolarPanel(double energy){
        this.energy = energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public float readValue() {
        return 0;
    }
}
