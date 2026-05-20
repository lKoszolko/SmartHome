import org.example.managers.AiAssistantImpl;
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
        AiAssistantImpl assistant = new AiAssistantImpl(TEST_LOG);
        assertFalse(assistant.isActive(assistant.getIonization()));
    }

    @Test
    public void shouldTurnOffDevice() {
        AiAssistantImpl assistant = new AiAssistantImpl(TEST_LOG);
        assistant.turnOn(assistant.getIonization());
        assistant.turnOff(assistant.getIonization());
        assertFalse(assistant.isActive(assistant.getIonization()));
    }

    @Test
    public void shouldTurnOnDevice() {
        AiAssistantImpl assistant = new AiAssistantImpl(TEST_LOG);
        assistant.turnOn(assistant.getIonization());
        assertTrue(assistant.isActive(assistant.getIonization()));
    }
}
