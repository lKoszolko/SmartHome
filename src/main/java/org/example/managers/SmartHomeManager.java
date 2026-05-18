package org.example.managers;

import org.example.models.*;
import org.example.interfaces.ObslugaFunkcjonalnosciDomu;

import java.util.List;

public class SmartHomeManager {
    private final Inhabitant inhabitant;
    private AiAssistant assistantAi;

    private Camera camera;
    private Heating heating;
    private Light light;
    private AirIonization ionization;
    private SolarPanel solarPanel;
    private Speakers speakers;
    private Washing washing;

    public SmartHomeManager(Inhabitant inhabitant) {
        this.inhabitant = inhabitant;
    }

    public void initialize() {
        camera    = new Camera();
        heating   = new Heating();
        light     = new Light();
        ionization = new AirIonization();
        solarPanel = new SolarPanel(0);
        speakers  = new Speakers();
        washing   = new Washing();

        List<ObslugaFunkcjonalnosciDomu> list =
                List.of(camera, heating, light, ionization, speakers, washing);
        assistantAi = new AiAssistant(list);

        // Rejestracja obserwatora na wszystkich "Observed"
        camera.addObserver(assistantAi);
        heating.addObserver(assistantAi);
        washing.addObserver(assistantAi);
    }

    public boolean authorize(int pin) {
        return inhabitant.getPin() == pin;
    }

    public void activateEmergency() { }

    public void simulateEvent() {
        System.out.println("--- (SYMULACJA: Przechodzi listonosz) ---");
        camera.motionDetection();
    }

    // ── Gettery urządzeń ──────────────────────────────────────────────────────
    public AiAssistant getAssistantAi() { return assistantAi; }
    public Camera      getCamera()      { return camera;      }
    public Heating     getHeating()     { return heating;     }
    public Light       getLight()       { return light;       }
    public AirIonization getIonization(){ return ionization;  }
    public SolarPanel  getSolarPanel()  { return solarPanel;  }
    public Speakers    getSpeakers()    { return speakers;    }
    public Washing     getWashing()     { return washing;     }
}
