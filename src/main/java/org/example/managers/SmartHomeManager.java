package org.example.managers;

import org.example.models.*;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

import java.util.List;

public class SmartHomeManager {
    private final Inhabitant inhabitant;
    private AiAssistant assistantAi;

    // na potrzeby symulacji obiekt jako pole
    private final Camera camera = new Camera();

    public AiAssistant getAssistantAi() {
        return assistantAi;
    }

    public SmartHomeManager(Inhabitant inhabitant){
        this.inhabitant = inhabitant;
    }

    public void initialize(){

//        Camera camera = new Camera();

        Heating heating = new Heating();
        Light light = new Light();
        AirIonization ionization = new AirIonization();
        SolarPanel solarPanel = new SolarPanel(0);
        Speakers speakers = new Speakers();
        Washing washing = new Washing();

        List<ObslugaFunkcjonalnosciDomu> list = List.of(camera,heating,light,ionization,speakers,washing);
        assistantAi = new AiAssistant(list);

        //dodanie obserwatora zdarzeń (Asystent obserwuje podmioty)
        camera.addObserver(this.assistantAi);

    }

    public void activateEmergency(){
    }

    public boolean authorize(int pin){
        return inhabitant.getPin() == pin;
    }

    public void simulateEvent(){
        System.out.println("--- (SYMULACJA: Przechodzi listonosz) ---");
        camera.motionDetection();
    }

}
