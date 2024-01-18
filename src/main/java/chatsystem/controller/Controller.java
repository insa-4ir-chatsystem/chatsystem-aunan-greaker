package chatsystem.controller;

import java.io.IOException;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactList;
import chatsystem.network.udp.UDPSender;
import chatsystem.ui.ChatSystemGUI;

public class Controller {
    private static final Logger LOGGER = LogManager.getLogger(Controller.class);

    public static ChatSystemGUI gui;
	private static Boolean isOnline = false;
	private static String myUsername;

    public static Boolean isOnline() {
        return isOnline;
    }

    public static String getMyUsername() {
        return myUsername;
    }

    public static void setMyUsername(String myUsername) {
        Controller.myUsername = myUsername;
    }

    public static void setIsOnline(Boolean isOnline) {
        Controller.isOnline = isOnline;
    }


	public static void loginHandler(String availableUsername) {
		LOGGER.trace("Running loginHandler()...");
		if (isOnline) {
			LOGGER.error("Can't login because we are already online as " + myUsername);
			return;
		}
		myUsername = availableUsername;
		// Initilize the UDPListener
		UDPController.initilizeUDPListener();
		
		// Initilize the ContactList Observer
		ContactList.getInstance().addObserver(new ContactList.Observer() {
			@Override
			public void newContactAdded(Contact contact) {  
				// Update Contact table in GUI
				try {
					SwingUtilities.invokeLater(() -> {
    					gui.updateContactTable();
					});
					LOGGER.trace("Updated contact table in GUI");
				} catch (NullPointerException | NoClassDefFoundError e) {
					LOGGER.warn("Could not update view because GUI has not been initilized!");
				}
			}

			@Override
			public void contactRemoved(Contact contact) {
				// Update Contact table in GUI
				try {
					SwingUtilities.invokeLater(() -> {
    					gui.updateContactTable();
					});
					LOGGER.trace("Updated contact table in GUI");
				} catch (NullPointerException | NoClassDefFoundError e) {
					LOGGER.warn("Could not update view because GUI has not been initilized!");
				}
			}
		});

		try {
			// Sends its username on the network so others can add it to contactlist
			UDPSender.sendBroadcast(UDPController.BROADCAST_PORT, myUsername);
			LOGGER.trace("Sent UDP broadcast with username: " + myUsername + " on port: " + UDPController.BROADCAST_PORT);
		} catch (IOException e) {
			LOGGER.error("Failed to send UDP broadcast: " + e.getMessage());
		}

		TCPController.startTCPListener();
		gui = new ChatSystemGUI(); // Initilize the GUI

		isOnline = true;
		LOGGER.info("Now online with username: " + myUsername);
	}

    /** Logs the user out of the chatsystem*/
	public static void logoutHandler() {
		LOGGER.trace("Running logoutHandler()...");
		try {
			UDPSender.sendBroadcast(UDPController.BROADCAST_PORT, UDPController.LOGOUT_MSG);
			LOGGER.trace("Sent UDP broadcast with logout protocol: '" + UDPController.LOGOUT_MSG + "' on port: " + UDPController.BROADCAST_PORT);
		} catch (IOException e) {
			LOGGER.error("Failed to send UDP broadcast: " + e.getMessage());
		}
		
		gui.close();
		TCPController.stopTCPListener();
		UDPController.closeUDPListener();
		ContactList.getInstance().clear();
		isOnline = false;
	}
}
