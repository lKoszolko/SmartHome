package org.example.models;

import java.util.UUID;

public class Inhabitant {
    private final boolean isAtHome;
    private final String id;
    private final int pin;

    public Inhabitant(boolean isAtHome, int pin){
        this.isAtHome = isAtHome;
        this.id = UUID.randomUUID().toString();
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
