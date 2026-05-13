import org.example.managers.AiAssistant;
import org.example.models.AirIonization;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AiAssistantTest {
    @Test
    public void checkAddition_element_notNullList(){
        AirIonization ionization = new AirIonization();
        AiAssistant assistant = new AiAssistant();
        assistant.addDevice(ionization);
        assertTrue(assistant.getDevices().contains(ionization));
    }

    @Test
    public void shouldTurnOffDevice(){
        AirIonization airIonization = new AirIonization();
        AiAssistant assistant = new AiAssistant();
        airIonization.turnOn();
        assistant.turnOffDevice(airIonization);
        assertFalse(airIonization.checkStatus());
    }

    @Test
    public void shouldTurnOnDevice(){
        AirIonization airIonization = new AirIonization();
        AiAssistant assistant = new AiAssistant();
        assistant.turnOnDevice(airIonization);
        assertTrue(airIonization.checkStatus());
    }

}
