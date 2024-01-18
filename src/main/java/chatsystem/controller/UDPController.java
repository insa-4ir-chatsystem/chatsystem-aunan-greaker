package chatsystem.controller;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactAlreadyExists;
import chatsystem.contacts.ContactList;
import chatsystem.network.udp.UDPListener;
import chatsystem.network.udp.UDPMessage;
import chatsystem.network.udp.UDPSender;
import chatsystem.ui.ChatSystemGUI;
import chatsystem.ui.ChooseUsernameGUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UDPController {

    private static final Logger LOGGER = LogManager.getLogger(UDPController.class);
	public static final int BROADCAST_PORT = 7471; // The port on which all javaChatProgram instances must listen for Broadcast.
	public static final String ANNOUNCE_REQUEST_MSG = "All online users announce yourselves.";
	public static final String LOGOUT_MSG = "I am logging out.";
	public static ChatSystemGUI gui;
	public static Boolean isOnline = false;
	public static String myUsername;
	private static UDPListener udpListener;

    public static void contactDiscoveryMessageHandler(UDPMessage message) {
		switch (message.text()) {
			case ANNOUNCE_REQUEST_MSG:
				try {
					/** Broadcast our username on the network */
					UDPSender.send(message.source(), BROADCAST_PORT, myUsername);
					LOGGER.trace("Announced ourself to: " + message.source() + ":" + BROADCAST_PORT);
				} catch (IOException e) {
					LOGGER.error("Could not announce ourselves: " + e.getMessage());
				} catch (NullPointerException e) {
					LOGGER.error("Could not announce ourselves because myUsername is null: " + e.getMessage());
				}
				break;
			case LOGOUT_MSG:
				/**	Removes contact from contact list */
				Contact contactToRemove = ContactList.getInstance().getContact(message.source());
				ContactList.getInstance().removeContact(contactToRemove);
				LOGGER.info(contactToRemove + " logged out.");
				break;
			/**	Somebody connecting to the chat */
			default:
				Contact newContact = new Contact(message.text(), message.source());
				try {
					ContactList.getInstance().addContact(newContact);
					LOGGER.trace("New Contact added to the list: " + newContact);
					LOGGER.trace("ContactList: " + ContactList.getInstance().toString());
				} catch (ContactAlreadyExists e) {
					LOGGER.error("Received duplicated contact: " + newContact);
				}
				break;
		}
    }
    
    public static Boolean usernameAvailableHandler(String username) {
		LOGGER.trace("Checking if username '" + username + "' is available...");
		if (isOnline) {
			LOGGER.error("Could not check if username was available because we are already online as " + myUsername);
			return false;
		}
    	try {
            UDPListener server = new UDPListener(BROADCAST_PORT);
            server.addObserver(msg -> {UDPController.contactDiscoveryMessageHandler(msg);});
            server.start();
            
            myUsername = username;  // Gets the chosen username
			
    		try {
    			UDPSender.sendBroadcast(BROADCAST_PORT, ANNOUNCE_REQUEST_MSG); // Sends ANNOUNCE msg to request online users to announce themselves.
    			Thread.sleep(2000); // Waits 2 seconds for all online users to announce themselves
    		} catch (IOException e) {
    			LOGGER.error("Could not start send broadcast: " + e.getMessage());
    		} catch (InterruptedException e) {
    			LOGGER.error("Could not sleep thread ContactList may not have been initialised correctly: " + e.getMessage());
    		}    
    		    
			/** Creates itself has a contact to check if the username is already taken */
    		Contact newContact = new Contact(myUsername, InetAddress.getLoopbackAddress());
    		if (!ContactList.getInstance().hasContact(newContact)) {
    			// Username chosen is available
    			server.close();
    		    return true;
    		}
    		else {
    			// Username chosen is not available / already taken
    			server.close();
    		    return false;
    		}  		
        } catch (SocketException e) {
            System.err.println("Could not start UDP listener: " + e.getMessage());
            System.exit(1);
        }	
    	return false;
    }

    public static void initilizeUDPListener() {
		try {
			udpListener = new UDPListener(BROADCAST_PORT);
			udpListener.addObserver(msg -> {UDPController.contactDiscoveryMessageHandler(msg);});
			udpListener.start();
		} catch (SocketException e) {
			LOGGER.fatal("Could not start UDP listener: " + e.getMessage());
		}	
	}

	public static void closeUDPListener() {
		try {
			udpListener.close();
		} catch (NullPointerException e) {
			LOGGER.warn("Tried to close UDPListener but it was not running");
		}
	}

	public static void loginHandler(String availableUsername) {
		LOGGER.trace("Running loginHandler()...");
		if (isOnline) {
			LOGGER.error("Can't login because we are already online as " + myUsername);
			return;
		}
		myUsername = availableUsername;
		// Initilize the UDPListener
		initilizeUDPListener();
		
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
			UDPSender.sendBroadcast(BROADCAST_PORT, myUsername);
			LOGGER.trace("Sent UDP broadcast with username: " + myUsername + " on port: " + BROADCAST_PORT);
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
			UDPSender.sendBroadcast(BROADCAST_PORT, LOGOUT_MSG);
			LOGGER.trace("Sent UDP broadcast with logout protocol: '" + LOGOUT_MSG + "' on port: " + BROADCAST_PORT);
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