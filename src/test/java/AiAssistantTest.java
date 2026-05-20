import org.example.managers.AiAssistant;
import org.example.managers.AiAssistant.DeviceName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class AiAssistantTest {

    private static final String TEST_LOG = "test_logs.json";

    @AfterEach
    void cleanup() {
        new File(TEST_LOG).delete();
    }
    @Test
    public void checkAddition_element_notNullList() {
        AiAssistant assistant = new AiAssistant(TEST_LOG);
        assertFalse(assistant.isActive(DeviceName.JONIZACJA));
    }

    @Test
    public void shouldTurnOffDevice() {
        AiAssistant assistant = new AiAssistant(TEST_LOG);
        assistant.turnOn(DeviceName.JONIZACJA);
        assistant.turnOff(DeviceName.JONIZACJA);
        assertFalse(assistant.isActive(DeviceName.JONIZACJA));
    }

    @Test
    public void shouldTurnOnDevice() {
        AiAssistant assistant = new AiAssistant(TEST_LOG);
        assistant.turnOn(DeviceName.JONIZACJA);
        assertTrue(assistant.isActive(DeviceName.JONIZACJA));
    }
}