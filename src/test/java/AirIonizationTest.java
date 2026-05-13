import org.example.models.AirIonization;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AirIonizationTest {
    @Test
    public void shouldTurnOffIonization(){
        AirIonization ionization = new AirIonization();
        ionization.turnOn();
        assertTrue(ionization.checkStatus());
    }

    @Test
    public void shouldTurnOffWhenActive(){
        AirIonization ionization = new AirIonization();
        ionization.turnOn();
        ionization.turnOff();
        assertFalse(ionization.checkStatus());
    }
}
