package chatsystem.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import chatsystem.ui.ChatSystemGUI;

@TestInstance(Lifecycle.PER_METHOD)
class ControllerTest {

    @BeforeEach
    void setUp() {
        Controller.setIsOnline(false);
        Controller.setMyUsername(null);
        Controller.setGui(null);
    }
    @Test
    void loginHandler_ShouldSetIsOnlineTrueAndInitializeComponents() {
        assertFalse(Controller.isOnline());

        // Mock the GUI to check if it is initialized
        ChatSystemGUI mockGUI = new ChatSystemGUI();
        Controller.setMyUsername("testUser");
        Controller.setGui(mockGUI);

        // Call the method under test
        Controller.loginHandler("testUser");

        // Assert that isOnline is true and GUI is initialized
        assertTrue(Controller.isOnline());
        assertNotNull(Controller.getGui());
    }

    @Test
    void loginHandler_ShouldNotLoginIfAlreadyOnline() {
        // Set isOnline to true
        Controller.setIsOnline(true);

        // Mock the GUI to check if it is initialized
        ChatSystemGUI mockGUI = new ChatSystemGUI();
        Controller.setMyUsername("testUser");
        Controller.setGui(mockGUI);

        // Call the method under test
        Controller.loginHandler("testUser");

        // Assert that isOnline remains true and GUI is not reinitialized
        assertTrue(Controller.isOnline());
        assertEquals(mockGUI, Controller.getGui());
    }

    @Test
    void logoutHandler_ShouldSetIsOnlineFalseAndCloseComponents() {
        // Set isOnline to true
        Controller.setIsOnline(true);

        // Mock the GUI to check if it is closed
        ChatSystemGUI mockGUI = new ChatSystemGUI();
        Controller.setGui(mockGUI);

        // Call the method under test
        Controller.logoutHandler();

        // Assert that isOnline is false and GUI is closed
        assertFalse(Controller.isOnline());
        assertNull(Controller.getGui());
    }
}
