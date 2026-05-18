package org.example;


import org.example.interfaces.IObserver;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.managers.AiAssistant;
import org.example.managers.SmartHomeManager;
import org.example.models.Camera;
import org.example.models.Inhabitant;
import org.example.models.Washing;

class Main {
    static void main(String[] args){
        Inhabitant inhabitant = new Inhabitant(true, 1234);
        SmartHomeManager smartHomeManager = new SmartHomeManager(inhabitant);
        if (smartHomeManager.authorize(1234)){
            smartHomeManager.initialize();
        } else {
            System.out.println("uzytkownik niezalogowany");
        }

        AiAssistant assistant = smartHomeManager.getAssistantAi();
        Camera camera = new Camera();
        smartHomeManager.simulateEvent();



    }
}
