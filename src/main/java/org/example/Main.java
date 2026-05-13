package org.example;


import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.managers.AiAssistant;
import org.example.models.*;

import java.util.List;

class Main {
    static void main(String[] args){
        Camera camera = new Camera();
        Heating heating = new Heating();
        Light light = new Light();
        AirIonization ionization = new AirIonization();
        SolarPanel solarPanel = new SolarPanel(0);
        Speakers speakers = new Speakers();
        Washing washing = new Washing();

        List<ObslugaFunkcjonalnosciDomu> list = List.of(camera,heating,light,ionization,speakers,washing);

        AiAssistant aiAssistant = new AiAssistant(list);

        list.forEach(ObslugaFunkcjonalnosciDomu::turnOn);

        aiAssistant.printActiveDevices(list);

        aiAssistant.turnOffDevice(ionization);

        System.out.println("\n");

        aiAssistant.printActiveDevices(list);

    }
}
