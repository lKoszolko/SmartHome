package org.example.managers;

import org.example.models.Inhabitant;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

import java.util.List;

public class SmartHomeManager {
    Inhabitant inhabitant;
    AiAssistant assistant = new AiAssistant();
    public void initialize(List<ObslugaFunkcjonalnosciDomu> o){
    }

    public void activateEmergency(){
    }

    public boolean authorize(int pin){
        return inhabitant.getPin() == pin;
    }

}
