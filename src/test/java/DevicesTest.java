import org.example.interfaces.ObslugaFunkcjonalnosciDomu;
import org.example.models.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DevicesTest {
    @Test
    public void checkActivate_turnedOffDevices_turnOnDevices(){
        AirIonization ionization = new AirIonization();
        Camera camera = new Camera();
        Heating heating = new Heating();
        Light light = new Light();
        Washing washing = new Washing();

        List<ObslugaFunkcjonalnosciDomu> list = List.of(ionization, camera, heating, light, washing);
        list.forEach(ObslugaFunkcjonalnosciDomu::turnOn);
        boolean active = list.stream().allMatch(ObslugaFunkcjonalnosciDomu::checkStatus);
        assertTrue(active);

    }

    @Test
    public void checkActivate_turnedOnDevices_turnOffDevices(){
        AirIonization ionization = new AirIonization();
        Camera camera = new Camera();
        Heating heating = new Heating();
        Light light = new Light();
        Washing washing = new Washing();

        ionization.turnOn();
        camera.turnOn();
        heating.turnOn();
        light.turnOn();
        washing.turnOn();

        List<ObslugaFunkcjonalnosciDomu> list = List.of(ionization, camera, heating, light, washing);
        list.forEach(ObslugaFunkcjonalnosciDomu::turnOff);

    }
}
