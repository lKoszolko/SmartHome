package org.example;

import org.example.managers.SmartHomeManager;
import org.example.models.Inhabitant;

public class Main {
    public static void main(String[] args) {
        Inhabitant inhabitant = new Inhabitant(true, 1234);
        SmartHomeManager manager = new SmartHomeManager(inhabitant);
        new ConsoleUI(manager).start();
    }
}
