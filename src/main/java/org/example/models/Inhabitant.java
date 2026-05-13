package org.example.models;

public class Inhabitant {
    private final boolean isAtHome;
    private final String id;
    private final int pin;

    public Inhabitant(boolean isAtHome, String id, int pin){
        this.isAtHome = isAtHome;
        this.id = id;
        this.pin = pin;
    }

    public boolean isAtHome() {
        return isAtHome;
    }

    public String getId() {
        return id;
    }

    public int getPin() {
        return pin;
    }
}
