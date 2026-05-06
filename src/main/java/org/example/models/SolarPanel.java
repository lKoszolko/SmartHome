package org.example.models;


import org.example.interfaces.Sensors;

public class SolarPanel implements Sensors {

    private float energy;

    public SolarPanel(float energy){
        this.energy = energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    @Override
    public float readValue() {
        return energy;
    }
}
