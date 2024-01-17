package chatsystem.controller;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactAlreadyExists;
import chatsystem.contacts.ContactList;
import chatsystem.network.udp.UDPListener;
import chatsystem.network.udp.UDPMessage;
import chatsystem.network.udp.UDPSender;
import chatsystem.ui.ChatSystemGUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UDPController {

    private static final Logger LOGGER = LogManager.getLogger(UDPController.class);
	public static final int BROADCAST_PORT = 7471; // The port on which all javaChatProgram instances must listen for Broadcast.
	public static final String ANNOUNCE_PROTOCOL = "All online users announce yourselves.";
	public static final String LOGOUT_PROTOCOL = "I am logging out.";
	public static String myUsername;

    public static void contactDiscoveryMessageHandler(UDPMessage message) {
		// Somebody announced themselves, so we add them to our contact list.
    	if (!message.text().equals(ANNOUNCE_PROTOCOL))
    	{
	    	Contact contact = new Contact(message.text(), message.source());
	        try {
	            ContactList.getInstance().addContact(contact);
	            LOGGER.trace("New Contact added to the list: " + contact);
				LOGGER.trace("ContactList: " + ContactList.getInstance());
	        } catch (ContactAlreadyExists e) {
	            LOGGER.error("Received duplicated contact: " + contact);
	        }
    	}
		// Somebody logged out, so we remove them from our contact list.
		else if (message.text().equals(LOGOUT_PROTOCOL)) {
			Contact contact = new Contact(message.text(), message.source());
			ContactList.getInstance().removeContact(contact);
			LOGGER.info(contact + " logged out.");
		}
		// Somebody asked us to announce ourselves, so we do.
    	else {
    		try {
				UDPSender.send(message.source(), BROADCAST_PORT, myUsername);
				LOGGER.trace("Announced ourself to: " + message.source() + ":" + BROADCAST_PORT);
			} catch (IOException e) {
				LOGGER.error("Could not announce ourselves: " + e.getMessage());
			}
    	}
    }
    
    public static Boolean usernameAvailableHandler(String username) {
		LOGGER.trace("Checking if username '" + username + "' is available...");
    	try {
            UDPListener server = new UDPListener(BROADCAST_PORT);
            server.addObserver(msg -> {UDPController.contactDiscoveryMessageHandler(msg);});
            server.start();
            
            myUsername = username;  // Gets the chosen username
			
    		try {
    			UDPSender.sendBroadcast(BROADCAST_PORT, ANNOUNCE_PROTOCOL); // Sends ANNOUNCE msg to request online users to announce themselves.
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
			UDPListener server = new UDPListener(BROADCAST_PORT);
			server.addObserver(msg -> {UDPController.contactDiscoveryMessageHandler(msg);});
			server.start();
		} catch (SocketException e) {
			System.err.println("Could not start UDP listener: " + e.getMessage());
			System.exit(1);
		}	
	}

	public static void loginHandler() {
		LOGGER.trace("Running loginHandler...");
		// Initilize the UDPListener
		initilizeUDPListener();
		
		// Initilize the ContactList Observer
		ContactList.getInstance().addObserver(new ContactList.Observer() {
			@Override
			public void newContactAdded(Contact contact) {  
				// Update Contact table in GUI
				ChatSystemGUI.updateContactTable();
				LOGGER.trace("Updated contact table in GUI");
			}

			@Override
			public void contactRemoved(Contact contact) {
				// Update Contact table in GUI
				ChatSystemGUI.updateContactTable();
				LOGGER.trace("Updated contact table in GUI");
			}

			@Override
			public void nicknameChanged(Contact newContact, String previousNickname) {
				// Handle nickname change
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
		LOGGER.info("Now online with username: " + myUsername);
	}

	/** To be run when closing main chatsystem window */
	public static void onExit() {
		LOGGER.trace("Running onExit...");
		try {
			UDPSender.sendBroadcast(BROADCAST_PORT, LOGOUT_PROTOCOL);
			LOGGER.trace("Sent UDP broadcast with logout protocol: " + LOGOUT_PROTOCOL + " on port: " + BROADCAST_PORT);
		} catch (IOException e) {
			LOGGER.error("Failed to send UDP broadcast: " + e.getMessage());
		}
		TCPController.stopTCPListener();
	}
}